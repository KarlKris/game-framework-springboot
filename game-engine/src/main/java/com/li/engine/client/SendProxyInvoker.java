package com.li.engine.client;

import com.li.engine.protocol.MessageFactory;
import com.li.engine.service.VocationalWorkConfig;
import com.li.engine.service.handler.ThreadSessionIdentityHolder;
import com.li.engine.service.rpc.SocketFutureManager;
import com.li.engine.service.rpc.future.RpcSocketFuture;
import com.li.gamecommon.ApplicationContextHolder;
import com.li.gamecommon.exception.BadRequestException;
import com.li.gamecommon.exception.SocketException;
import com.li.gamecommon.exception.code.ServerErrorCode;
import com.li.gamecommon.utils.IpUtils;
import com.li.gamecommon.utils.ObjectsUtil;
import com.li.network.message.InnerMessage;
import com.li.network.message.ProtocolConstant;
import com.li.network.protocol.InBodyMethodParameter;
import com.li.network.protocol.MethodCtx;
import com.li.network.protocol.MethodParameter;
import com.li.network.serialize.SerializerHolder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author li-yuanwen
 * rpc代理实际执行器
 */
@Slf4j
public class SendProxyInvoker implements InvocationHandler {

    /** 连接对方的Client **/
    private final NioNettyClient client;
    /** 方法参数上下文 **/
    private final Map<Method, MethodCtx> methodCtxHolder;
    /** 远程调用消息Future容器 **/
    private final SocketFutureManager socketFutureManager = ApplicationContextHolder.getBean(SocketFutureManager.class);
    /** 消息工厂 **/
    private final MessageFactory messageFactory = ApplicationContextHolder.getBean(MessageFactory.class);
    /** 超时时间(秒) **/
    private final int timeoutSecond = ApplicationContextHolder.getBean(VocationalWorkConfig.class).getTimeoutSecond();

    public SendProxyInvoker(NioNettyClient client, List<MethodCtx> methodCtx) {
        this.client = client;
        this.methodCtxHolder = new HashMap<>(methodCtx.size());
        methodCtx.forEach(k -> this.methodCtxHolder.putIfAbsent(k.getMethod(), k));
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (ObjectsUtil.OBJECT_METHODS.contains(method)) {
            return method.invoke(proxy, args);
        }

        MethodCtx methodCtx = this.methodCtxHolder.get(method);
        if (methodCtx == null) {
            throw new IllegalArgumentException("远程方法[" + method.getName() + "]没有添加 @SocketCommand 注解");
        }

        byte[] body = null;
        MethodParameter[] params = methodCtx.getParams();
        for (int i = 0; i < args.length; i++) {
            if (params[i] instanceof InBodyMethodParameter) {
                body = SerializerHolder.DEFAULT_SERIALIZER.serialize(args[i]);
                break;
            }
        }

        InnerMessage message = messageFactory.toInnerMessage(socketFutureManager.nextSn()
                , ProtocolConstant.VOCATIONAL_WORK_REQ
                , methodCtx.getProtocol()
                , SerializerHolder.DEFAULT_SERIALIZER.getSerializerType()
                , body
                , ThreadSessionIdentityHolder.getIdentity()
                , IpUtils.getLocalIpAddress());

        try {
            CompletableFuture<Object> future = client.send(message, (msg, completableFuture)
                    -> socketFutureManager.addSocketFuture(new RpcSocketFuture(msg.getSn(), method.getReturnType(), completableFuture)));

            return future.get(timeoutSecond, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException e) {
            log.error("SendProxyInvoker超时中断", e);
            throw new SocketException(ServerErrorCode.TIME_OUT);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof BadRequestException) {
                throw (BadRequestException) cause;
            }
            if (cause instanceof SocketException) {
                throw (SocketException) cause;
            }
            log.error("SendProxyInvoker发生未知ExecutionException异常", e);
            throw new SocketException(ServerErrorCode.UNKNOWN);
        } catch (Exception e) {
            log.error("SendProxyInvoker发生未知异常", e);
            throw new SocketException(ServerErrorCode.UNKNOWN);
        }
    }
}
