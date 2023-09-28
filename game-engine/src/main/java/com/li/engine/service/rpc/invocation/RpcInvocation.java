package com.li.engine.service.rpc.invocation;

import cn.hutool.core.util.ArrayUtil;
import com.li.common.ApplicationContextHolder;
import com.li.common.exception.SocketException;
import com.li.network.message.InnerMessage;
import com.li.network.message.SocketProtocol;
import com.li.network.modules.ErrorCode;
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
public class RpcInvocation extends Invocation {

    /** rpc结果回调future **/
    private final CompletableFuture<Object> future;
    /** 回调结果类型 **/
    private final SocketProtocolManager socketProtocolManager;

    public RpcInvocation(long sn, Long parentSn, long identity, boolean sync
            , SocketProtocolManager socketProtocolManager, CompletableFuture<Object> future) {
        super(sn, parentSn, identity, sync);
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
            ErrorCode errorCode = serializer.deserialize(message.getBody(), ErrorCode.class);
            future.completeExceptionally(new SocketException(errorCode.getCode(), "请求远程服务异常"));
            return;
        }
        ProtocolMethodCtx protocolMethodCtx = socketProtocolManager.getMethodCtxBySocketProtocol(protocol);
        Object result = null;
        if (ArrayUtil.isNotEmpty(message.getBody())) {
            result = serializer.deserialize(message.getBody(), protocolMethodCtx.getReturnClz());
        }
        future.complete(result);
    }

    public CompletableFuture<Object> getFuture() {
        return future;
    }
}
