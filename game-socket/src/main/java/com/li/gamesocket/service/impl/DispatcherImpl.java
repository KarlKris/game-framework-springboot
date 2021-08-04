package com.li.gamesocket.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.ZipUtil;
import com.li.gamecore.rpc.RemoteServerSeekService;
import com.li.gamecore.rpc.model.Address;
import com.li.gamecore.thread.MonitoredThreadPoolExecutor;
import com.li.gamesocket.client.NioNettyClient;
import com.li.gamesocket.client.NioNettyClientFactory;
import com.li.gamesocket.exception.BadRequestException;
import com.li.gamesocket.exception.MethodParamAnalysisException;
import com.li.gamesocket.exception.SerializeFailException;
import com.li.gamesocket.protocol.*;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerManager;
import com.li.gamesocket.service.*;
import com.li.gamesocket.session.Session;
import com.li.gamesocket.session.SessionManager;
import com.li.gamesocket.session.SnCtxManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
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
    private NioNettyClientFactory clientFactory;
    @Autowired(required = false)
    private RemoteServerSeekService remoteServerSeekService;
    @Autowired(required = false)
    private SnCtxManager snCtxManager;

    /** 业务线程池 **/
    private ExecutorService[] executorServices;



    @PostConstruct
    private void init() {

        // 保证线程池数量是2的N次幂
        int i = (Runtime.getRuntime().availableProcessors() >> 1) << 1;
        this.executorServices = new ExecutorService[i];
        for (int j = 0; j < i; j++) {
            // 单线程池,减少加锁频率
            this.executorServices[j] = new MonitoredThreadPoolExecutor(1,1,
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
        this.executorServices[index].submit(()-> { doDispatch(session, message); });


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

        // 方法调用上下文
        MethodInvokeCtx methodInvokeCtx = commandManager.getMethodInvokeCtx(message.getCommand());
        if (methodInvokeCtx == null) {
            // RPC
            if (this.remoteServerSeekService == null) {
                response(session, message, serializer.serialize(Response.INVALID_OP), false);
                return;
            }

            Address address = this.remoteServerSeekService.seekApplicationAddressByCommand(message.getCommand().getModule()
                    , message.getCommand().getInstruction());

            if (address == null || !forwardMessage(message, session, address)) {
                response(session, message, serializer.serialize(Response.INVALID_OP), false);
                return;
            }

            return;
        }

        Class<?> returnType = methodInvokeCtx.getMethod().getReturnType();
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
                if (identity <= 0) {
                    // 没有标识
                    response(session, message, serializer.serialize(Response.NO_IDENTITY), false);
                    return;
                }

                result = ReflectionUtils.invokeMethod(methodInvokeCtx.getMethod(), methodInvokeCtx.getTarget()
                        , decodeRequest(innerMessage.getIdentity(), methodInvokeCtx.getParams(), request));
            }else {

                if (methodInvokeCtx.isIdentity() && !session.identity()) {
                    // 没有标识
                    response(session, message, serializer.serialize(Response.NO_IDENTITY), false);
                    return;
                }

                result = ReflectionUtils.invokeMethod(methodInvokeCtx.getMethod(), methodInvokeCtx.getTarget()
                        , decodeRequest(session.getIdentity(), methodInvokeCtx.getParams(), request));
            }

            Response response;
            if (!noResponse) {
                // 响应
                response = Response.SUCCESS(result);
            }else {
                response = Response.DEFAULT_SUCCESS;
            }
            responseBody = serializer.serialize(response);
            zip = responseBody.length > config.getBodyZipLength();
            if (zip) {
                responseBody = ZipUtil.gzip(responseBody);
            }
        }catch (SerializeFailException e) {
            log.error("发生序列化/反序列化异常", e);
            responseBody = serializer.serialize(Response.SERIALIZE_FAIL);
        }catch (ConvertException e) {
            log.error("发生类型转换异常", e);
            responseBody = serializer.serialize(Response.CONVERT_FAIL);
        }catch (MethodParamAnalysisException e){
            log.error("发生参数解析异常", e);
            responseBody = serializer.serialize(Response.PARAM_ANALYSIS_ERROR);
        }catch (BadRequestException e){
            if (log.isDebugEnabled()) {
                log.debug("发生异常请求异常,异常码[{}]", e.getErrorCode(), e);
            }
            responseBody = serializer.serialize(Response.ERROR(e.getErrorCode()));
        }finally {
            response(session, message, responseBody, zip);
        }
    }

    /** 解析出方法参数 **/
    private Object[] decodeRequest(long identity, MethodParameter[] params, Request request) {
        Map<String, Object> map = request.getParams();

        int length = params.length;
        Object[] objs = new Object[length];
        for (int i = 0; i < length; i++) {
            MethodParameter parameter = params[i];
            if (parameter.identity()) {
                objs[i] = identity;
                continue;
            }

            String parameterName = parameter.getParameterName();
            Object o = map.get(parameterName);
            if (o != null) {
                objs[i] = Convert.convert(parameter.getParameterType(), o);
                continue;
            }

            if (!parameter.isRequired()) {
                objs[i] = null;
                continue;
            }

            throw new MethodParamAnalysisException("未提供参数[" + parameterName + "]");
        }

        return objs;
    }

    /** 转发消息 **/
    private boolean forwardMessage(IMessage message, Session session, Address address) {
        NioNettyClient client = clientFactory.newInstance(address);
        long nextSn = snCtxManager.nextSn();
        InnerMessage innerMessage = MessageFactory.transformInnerRequest(message, nextSn, session);

        try {
            client.send(innerMessage
                    , (msg, completableFuture)
                            -> snCtxManager.forward(msg.getSn(), nextSn, session.getChannel()));
            return true;
        } catch (InterruptedException e) {
            log.error("消息转发至[{}]发生未知异常", address, e);
            return false;
        }
    }

    /** 响应 **/
    private void response(Session session, IMessage message, byte[] responseBody, boolean zip) {
        sessionManager.writeAndFlush(session, MessageFactory.transformResponseMsg(session, message, responseBody, zip));
    }


    /**
     * 根据hash找到对应的线程池下标,仿HashMap
     **/
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
