package com.li.gamesocket.service.rpc.future;

import com.li.gamecommon.ApplicationContextHolder;
import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerHolder;
import com.li.gamesocket.service.protocol.SocketProtocol;

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
    private final Class<?> clazz;

    public RpcSocketFuture(long sn, Class<?> clazz, CompletableFuture<Object> future) {
        super(sn);
        this.future = future;
        this.clazz = clazz;
    }

    @Override
    public void complete(IMessage message) {
        Serializer serializer = ApplicationContextHolder.getBean(SerializerHolder.class).getSerializer(message.getSerializeType());
        SocketProtocol protocol = message.getProtocol();
        // todo 如果是返回错误码
        if (protocol.getModule() == 1) {
            // todo 解析错误码消息
            // 业务异常码
//            future.completeExceptionally(new BadRequestException(response.getCode(), "请求远程服务业务异常"));
            // rpc异常
//            future.completeExceptionally(new SocketException(response.getCode(), "请求远程服务通讯异常"));
            return;
        }
        Object result = serializer.deserialize(message.getBody(), clazz);
        future.complete(result);
    }
}
