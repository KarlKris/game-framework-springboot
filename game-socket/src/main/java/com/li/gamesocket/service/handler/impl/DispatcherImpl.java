package com.li.gamesocket.service.handler.impl;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.ZipUtil;
import com.li.gamecore.exception.BadRequestException;
import com.li.gamecore.exception.SerializeFailException;
import com.li.gamecore.thread.MonitoredThreadPoolExecutor;
import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.protocol.*;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerManager;
import com.li.gamesocket.service.VocationalWorkConfig;
import com.li.gamesocket.service.command.CommandManager;
import com.li.gamesocket.service.command.MethodCtx;
import com.li.gamesocket.service.command.MethodInvokeCtx;
import com.li.gamesocket.service.handler.Dispatcher;
import com.li.gamesocket.service.rpc.RpcService;
import com.li.gamesocket.service.session.Session;
import com.li.gamesocket.service.session.SessionManager;
import com.li.gamesocket.utils.CommandUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 * @date 2021/7/31 15:44
 **/
@Component
@Slf4j
public class DispatcherImpl implements Dispatcher, ApplicationListener<ContextClosedEvent> {


    @Autowired
    private VocationalWorkConfig config;
    @Autowired
    private CommandManager commandManager;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private SerializerManager serializerManager;

    @Autowired
    private RpcService rpcService;

    /** 业务线程池 **/
    private ExecutorService[] executorServices;


    @PostConstruct
    private void init() {

        // 保证线程池数量是2的N次幂
        int i = (Runtime.getRuntime().availableProcessors() >> 1) << 1;
        this.executorServices = new ExecutorService[i];
        for (int j = 0; j < i; j++) {
            // 单线程池,减少加锁频率
            this.executorServices[j] = new MonitoredThreadPoolExecutor(1, 1,
                    0, TimeUnit.SECONDS
                    , new ArrayBlockingQueue<>(config.getMaxQueueLength())
                    , new NamedThreadFactory("业务线程池", false));
        }
    }


    @Override
    public void dispatch(IMessage message, Session session) {
        if (!message.isRequest()) {
            if (log.isDebugEnabled()) {
                log.debug("服务器收到响应消息,忽略");
            }
            return;
        }

        long id = session.getSessionId();
        if (session.identity()) {
            id = session.getIdentity();
        }

        int index = canAndGetExecutorServiceArrayIndex(hash(id));
        this.executorServices[index].submit(() -> {
            doDispatch(session, message);
        });


    }

    /** 分发消息 **/
    private void doDispatch(Session session, IMessage message) {
        // 查询序列化/反序列化方式
        byte serializeType = message.getSerializeType();
        Serializer serializer = serializerManager.getSerializer(serializeType);
        if (serializer == null) {
            if (log.isWarnEnabled()) {
                log.warn("请求消息序列化类型[{}],找不到对应的序列化工具,忽略", serializeType);
            }
            return;
        }

        // 记录序列化/反序列化方式
        session.getChannel().attr(ChannelAttributeKeys.LAST_SERIALIZE_TYPE).set(serializeType);

        // 方法调用上下文
        MethodInvokeCtx methodInvokeCtx = commandManager.getMethodInvokeCtx(message.getCommand());
        if (methodInvokeCtx == null) {
            // RPC
            if (!this.rpcService.forward(session, message)) {
                response(session, message, serializer.serialize(Response.INVALID_OP), false);
            }
            return;
        }

        MethodCtx methodCtx = methodInvokeCtx.getMethodCtx();
        Class<?> returnType = methodCtx.getMethod().getReturnType();
        boolean noResponse = ClassUtils.isAssignable(Void.TYPE, returnType);

        byte[] body = message.getBody();
        if (message.zip()) {
            body = ZipUtil.unGzip(body);
        }

        byte[] responseBody = null;
        boolean zip = false;
        try {
            Request request = serializer.deserialize(body, Request.class);

            Object result = null;
            if (message.isInnerMessage()) {
                InnerMessage innerMessage = (InnerMessage) message;

                long identity = innerMessage.getIdentity();

                if (methodInvokeCtx.isIdentity() && identity <= 0) {
                    // 没有标识
                    response(session, message, serializer.serialize(Response.NO_IDENTITY), false);
                    return;
                }

                result = ReflectionUtils.invokeMethod(methodCtx.getMethod(), methodInvokeCtx.getTarget()
                        , CommandUtils.decodeRequest(session, innerMessage.getIdentity(), methodCtx.getParams(), request));
            } else {

                if (methodInvokeCtx.isIdentity() && !session.identity()) {
                    // 没有标识
                    response(session, message, serializer.serialize(Response.NO_IDENTITY), false);
                    return;
                }

                result = ReflectionUtils.invokeMethod(methodCtx.getMethod(), methodInvokeCtx.getTarget()
                        , CommandUtils.decodeRequest(session, session.getIdentity(), methodCtx.getParams(), request));
            }

            Response<Object> response;
            if (!noResponse) {
                // 响应
                if (result instanceof Response) {
                    response = (Response<Object>) result;
                }else {
                    response = Response.SUCCESS(result);
                }
            } else {
                response = Response.DEFAULT_SUCCESS;
            }
            responseBody = serializer.serialize(response);
            zip = responseBody.length > config.getBodyZipLength();
            if (zip) {
                responseBody = ZipUtil.gzip(responseBody);
            }
        } catch (SerializeFailException e) {
            log.error("发生序列化/反序列化异常", e);
            responseBody = serializer.serialize(Response.SERIALIZE_FAIL);
        } catch (ConvertException e) {
            log.error("发生类型转换异常", e);
            responseBody = serializer.serialize(Response.CONVERT_FAIL);
        } catch (IllegalArgumentException e) {
            log.error("发生参数解析异常", e);
            responseBody = serializer.serialize(Response.PARAM_ANALYSIS_ERROR);
        } catch (BadRequestException e) {
            if (log.isDebugEnabled()) {
                log.debug("发生异常请求异常,异常码[{}]", e.getErrorCode(), e);
            }
            responseBody = serializer.serialize(Response.ERROR(e.getErrorCode()));
        } finally {
            response(session, message, responseBody, zip);
        }
    }

    /** 响应 **/
    private void response(Session session, IMessage message, byte[] responseBody, boolean zip) {
        sessionManager.writeAndFlush(session, MessageFactory.transformResponseMsg(session, message, responseBody, zip));
    }


    /** 根据hash找到对应的线程池下标,仿HashMap **/
    private int canAndGetExecutorServiceArrayIndex(int hash) {
        int length = this.executorServices.length;
        return (length - 1) & hash;
    }

    // 计算hash值
    private static int hash(Long key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        if (log.isWarnEnabled()) {
            log.warn("关闭消息分发线程池数组");
        }

        for (ExecutorService executorService : this.executorServices) {
            executorService.shutdown();
        }
    }
}
