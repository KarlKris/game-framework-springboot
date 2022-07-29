package com.li.engine.client;

import com.li.common.exception.BadRequestException;
import com.li.common.exception.SocketException;
import com.li.common.exception.code.ServerErrorCode;
import com.li.common.utils.IpUtils;
import com.li.common.utils.ObjectUtils;
import com.li.engine.protocol.MessageFactory;
import com.li.engine.service.handler.ThreadLocalContentHolder;
import com.li.engine.service.rpc.InvocationManager;
import com.li.engine.service.rpc.invocation.RpcInvocation;
import com.li.network.message.InnerMessage;
import com.li.network.message.ProtocolConstant;
import com.li.network.protocol.InBodyMethodParameter;
import com.li.network.protocol.MethodParameter;
import com.li.network.protocol.ProtocolMethodCtx;
import com.li.network.protocol.SocketProtocolManager;
import com.li.network.serialize.SerializerHolder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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
    private final NettyClient client;
    /** 远程调用消息Future容器 **/
    private final InvocationManager invocationManager;
    /** 消息工厂 **/
    private final MessageFactory messageFactory;
    /** 超时时间(秒) **/
    private final int timeoutSecond;
    /** 协议管理器 **/
    private final SocketProtocolManager socketProtocolManager;

    public SendProxyInvoker(NettyClient client, InvocationManager invocationManager
            , MessageFactory messageFactory
            , SocketProtocolManager socketProtocolManager
            , int timeoutSecond) {
        this.client = client;
        this.invocationManager = invocationManager;
        this.messageFactory = messageFactory;
        this.timeoutSecond = timeoutSecond;
        this.socketProtocolManager = socketProtocolManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (ObjectUtils.OBJECT_METHODS.contains(method)) {
            return method.invoke(proxy, args);
        }

        ProtocolMethodCtx protocolMethodCtx = this.socketProtocolManager.getMethodCtxByMethod(method);
        if (protocolMethodCtx == null) {
            throw new IllegalArgumentException("远程方法[" + method.getName() + "]没有添加 @SocketCommand 注解");
        }

        byte[] body = null;
        MethodParameter[] params = protocolMethodCtx.getParams();
        for (int i = 0; i < args.length; i++) {
            if (params[i] instanceof InBodyMethodParameter) {
                body = SerializerHolder.DEFAULT_SERIALIZER.serialize(args[i]);
                break;
            }
        }

        final Long messageSn = ThreadLocalContentHolder.getMessageSn();
        final Long identity = ThreadLocalContentHolder.getIdentity();
        InnerMessage message = messageFactory.toInnerMessage(invocationManager.nextSn()
                , ProtocolConstant.VOCATIONAL_WORK_REQ
                , protocolMethodCtx.getProtocol()
                , SerializerHolder.DEFAULT_SERIALIZER.getSerializerType()
                , body
                , identity
                , IpUtils.getLocalIpAddress());

        try {
            Class<?> returnType = method.getReturnType();
            boolean sync = !returnType.isAssignableFrom(CompletableFuture.class);
            RpcInvocation rpcInvocation = new RpcInvocation(message.getSn(), messageSn, identity, sync
                    , socketProtocolManager, new CompletableFuture<>());

            client.send(message, rpcInvocation);

            if (!sync) {
                return rpcInvocation.getFuture();
            }

            return rpcInvocation.getFuture().get(timeoutSecond, TimeUnit.SECONDS);
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
