package com.li.gamesocket.client.channelhandler;

import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.exception.BadRequestException;
import com.li.gamesocket.exception.SocketException;
import com.li.gamesocket.messagesn.ForwardSnCtx;
import com.li.gamesocket.messagesn.RpcSnCtx;
import com.li.gamesocket.messagesn.SnCtx;
import com.li.gamesocket.messagesn.SnCtxManager;
import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.protocol.MessageFactory;
import com.li.gamesocket.protocol.Response;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerManager;
import com.li.gamesocket.session.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author li-yuanwen
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class ClientVocationalWorkHandler extends SimpleChannelInboundHandler<IMessage> {

    @Autowired
    private SnCtxManager snCtxManager;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private SerializerManager serializerManager;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMessage msg) throws Exception {
        // 处理从服务端收到的信息
        if (msg.isRequest()) {
            if (log.isDebugEnabled()) {
                log.debug("客户端ClientVocationalWorkHandler收到请求信息,忽略");
            }

            return;
        }

        SnCtx snCtx = this.snCtxManager.remove(msg.getSn());
        if (snCtx == null) {
            if (log.isDebugEnabled()) {
                log.debug("客户端ClientVocationalWorkHandler收到过期信息,序号[{}],忽略", msg.getSn());
            }

            return;
        }

        // 直接转发给源目标
        if (snCtx instanceof ForwardSnCtx) {

            ForwardSnCtx forwardSnCtx = (ForwardSnCtx) snCtx;

            Channel channel = forwardSnCtx.getChannel();
            Session session = channel.attr(ChannelAttributeKeys.SESSION).get();

            if (log.isDebugEnabled()) {
                log.debug("转发响应消息[{}]至[{}]", msg, session.ip());
            }

            IMessage message = MessageFactory.transformResponse(forwardSnCtx.getSn(), msg, session);

            sessionManager.writeAndFlush(session, message);

            return;
        }

        if (snCtx instanceof RpcSnCtx) {

            RpcSnCtx rpcSnCtx = (RpcSnCtx) snCtx;

            CompletableFuture<Response> future = rpcSnCtx.getFuture();

            Serializer serializer = serializerManager.getSerializer(msg.getSerializeType());
            Response response = serializer.deserialize(msg.getBody(), Response.class);
            if (response.success()) {
                future.complete(response);
            }else {
                if (response.isVocationalException()) {
                    future.completeExceptionally(new BadRequestException(response.getCode()));
                }else {
                    future.completeExceptionally(new SocketException(response.getCode()));
                }
            }

            return;
        }

        if (log.isWarnEnabled()) {
            log.warn("客户端ClientVocationalWorkHandler收到信息[{}],但不处理", snCtx);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.error("客户端发生IOException,与服务端断开连接", cause);
            ctx.close();
        }else {
            log.error("客户端发生未知异常", cause);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            // 开启心跳,则向对方发送心跳检测包
            if (event.state() == IdleState.WRITER_IDLE) {
                // 发生心跳检测包
                ctx.channel().writeAndFlush(MessageFactory.HEART_BEAT_REQ_INNER_MSG);
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
