package com.li.gamesocket.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.ZipUtil;
import com.li.gamesocket.exception.BadRequestException;
import com.li.gamesocket.exception.SerializeFailException;
import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.protocol.MessageFactory;
import com.li.gamesocket.protocol.Request;
import com.li.gamesocket.protocol.Response;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.service.*;
import com.li.gamesocket.session.Session;
import com.li.gamesocket.session.SessionManager;
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
import java.util.concurrent.ThreadPoolExecutor;
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
    private ApplicationContext applicationContext;

    /** 业务线程池 **/
    private ExecutorService[] executorServices;

    /** 消息体序列化器 **/
    private Map<Byte, Serializer> serializerHolder;

    @PostConstruct
    private void init() {
        this.serializerHolder = new HashMap<>(2);
        for (Serializer serializer : applicationContext.getBeansOfType(Serializer.class).values()) {
            if (this.serializerHolder.putIfAbsent(serializer.getSerializerType(), serializer) != null) {
                throw new BeanInitializationException("出现相同类型[" + serializer.getSerializerType() + "]序列化器");
            }
        }

        // 保证线程池数量是2的N次幂
        int i = (Runtime.getRuntime().availableProcessors() >> 1) << 1;
        this.executorServices = new ExecutorService[i];
        for (int j = 0; j < i; j++) {
            // todo 后续改成可监控的线程池(继承ThreadPoolExecutor)
            this.executorServices[j] = new ThreadPoolExecutor(1,1,
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
        this.executorServices[index].submit(()-> {
            doDispatch(session, message);
        });


    }

    /** 分发消息 **/
    private void doDispatch(Session session, IMessage message) {
        // 查询序列化/反序列化方式
        byte serializeType = message.getSerializeType();
        Serializer serializer = serializerHolder.get(serializeType);
        if (serializer == null) {
            if (log.isWarnEnabled()) {
                log.warn("请求消息序列化类型[{}],找不到对应的序列化工具,忽略", serializeType);
            }

            return;
        }

        // 方法调用上下文
        MethodInvokeCtx methodInvokeCtx = commandManager.getMethodInvokeCtx(message.getCommand());
        if (methodInvokeCtx == null) {
            // todo RPC
            return;
        }

        if (methodInvokeCtx.isIdentity() && !session.identity()) {
            // 没有标识
            response(session, message, serializer.serialize(Response.NO_IDENTITY), false);
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
            Object result = ReflectionUtils.invokeMethod(methodInvokeCtx.getMethod(), methodInvokeCtx.getTarget()
                    , decodeRequest(session, methodInvokeCtx.getParams(), request));

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
    private Object[] decodeRequest(Session session, MethodParameter[] params, Request request) {
        Map<String, Object> map = request.getParams();

        int length = params.length;
        Object[] objs = new Object[length];
        for (int i = 0; i < length; i++) {
            MethodParameter parameter = params[i];
            if (parameter.identity()) {
                objs[i] = session.getIdentity();
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

            // todo 报错
        }

        return objs;
    }

    /** 响应 **/
    private void response(Session session, IMessage message, byte[] responseBody, boolean zip) {
        sessionManager.writeAndFlush(session, MessageFactory.transformResponseMsg(message, responseBody, zip));
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

        int length = this.executorServices.length;
        for (int i = 0; i < length; i++) {
            this.executorServices[i].shutdown();
        }
    }
}
