package com.li.engine.service.rpc.future;

import com.li.common.ApplicationContextHolder;
import com.li.common.exception.SocketException;
import com.li.network.message.InnerMessage;
import com.li.network.message.SocketProtocol;
import com.li.network.modules.ErrorCodeModule;
import com.li.network.protocol.ProtocolMethodCtx;
import com.li.network.protocol.SocketProtocolManager;
import com.li.network.serialize.Serializer;
import com.li.network.serialize.SerializerHolder;

import java.util.concurrent.CompletableFuture;

/**
 * rpc调用Future
 * @author li-yuanwen
 * @date 2021/12/11
 */
public class RpcSocketFuture extends SocketFuture {

    /** rpc结果回调future **/
    private final CompletableFuture<Object> future;
    /** 回调结果类型 **/
    private final SocketProtocolManager socketProtocolManager;

    public RpcSocketFuture(long sn, long outerSn, long identity, boolean sync
            , SocketProtocolManager socketProtocolManager, CompletableFuture<Object> future) {
        super(sn, outerSn, identity, sync);
        this.future = future;
        this.socketProtocolManager = socketProtocolManager;
    }

    @Override
    public void complete(InnerMessage message) {
        Serializer serializer = ApplicationContextHolder.getBean(SerializerHolder.class).getSerializer(message.getSerializeType());
        SocketProtocol protocol = message.getProtocol();
        // 如果是返回错误码
        if (protocol.getModule() == ErrorCodeModule.MODULE) {
            // 解析错误码消息
            Integer errorCode = serializer.deserialize(message.getBody(), Integer.class);
            future.completeExceptionally(new SocketException(errorCode, "请求远程服务异常"));
            return;
        }
        ProtocolMethodCtx protocolMethodCtx = socketProtocolManager.getMethodCtxBySocketProtocol(protocol);
        Object result = serializer.deserialize(message.getBody(), protocolMethodCtx.getReturnClz());
        future.complete(result);
    }
}
