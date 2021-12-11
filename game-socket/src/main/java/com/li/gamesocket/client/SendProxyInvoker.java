package com.li.gamesocket.client;

import cn.hutool.core.util.ZipUtil;
import com.li.gamecommon.ApplicationContextHolder;
import com.li.gamecommon.exception.BadRequestException;
import com.li.gamecommon.exception.SocketException;
import com.li.gamecommon.exception.code.ServerErrorCode;
import com.li.gamecommon.utils.IpUtils;
import com.li.gamesocket.protocol.InnerMessage;
import com.li.gamesocket.protocol.MessageFactory;
import com.li.gamesocket.protocol.ProtocolConstant;
import com.li.gamesocket.protocol.Request;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerHolder;
import com.li.gamesocket.service.VocationalWorkConfig;
import com.li.gamesocket.service.handler.ThreadSessionIdentityHolder;
import com.li.gamesocket.service.protocol.MethodCtx;
import com.li.gamesocket.service.rpc.SocketFutureManager;
import com.li.gamesocket.service.rpc.future.RpcSocketFuture;
import com.li.gamesocket.utils.CommandUtils;
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
    /** 默认序列化/反序列化工具 **/
    private final Serializer serializer = ApplicationContextHolder.getBean(SerializerHolder.class).getDefaultSerializer();
    /** 默认压缩body阙值 **/
    private final int bodyZipLength = ApplicationContextHolder.getBean(VocationalWorkConfig.class).getBodyZipLength();
    /** 远程调用消息Future容器 **/
    private final SocketFutureManager socketFutureManager = ApplicationContextHolder.getBean(SocketFutureManager.class);
    /** 超时时间(秒) **/
    private final int timeoutSecond = ApplicationContextHolder.getBean(VocationalWorkConfig.class).getTimeoutSecond();

    public SendProxyInvoker(NioNettyClient client, List<MethodCtx> methodCtxes) {
        this.client = client;
        this.methodCtxHolder = new HashMap<>(methodCtxes.size());
        methodCtxes.forEach(k -> this.methodCtxHolder.putIfAbsent(k.getMethod(), k));
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        MethodCtx methodCtx = this.methodCtxHolder.get(method);
        if (methodCtx == null) {
            throw new IllegalArgumentException("远程方法[" + method.getName() + "]没有添加 @SocketCommand 注解");
        }

        Request request = CommandUtils.encodeRpcRequest(methodCtx.getParams(), args);

        byte[] body = serializer.serialize(request);

        boolean zip = false;
        if (body.length > bodyZipLength) {
            body = ZipUtil.gzip(body);
            zip = true;
        }

        InnerMessage message = MessageFactory.toInnerMessage(socketFutureManager.nextSn()
                , ProtocolConstant.VOCATIONAL_WORK_REQ
                , methodCtx.getProtocol()
                , serializer.getSerializerType()
                , zip
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
