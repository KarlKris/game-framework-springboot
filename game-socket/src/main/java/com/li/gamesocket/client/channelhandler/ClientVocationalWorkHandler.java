package com.li.gamesocket.client.channelhandler;

import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.client.NioNettyClient;
import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.protocol.MessageFactory;
import com.li.gamesocket.session.Session;
import com.li.gamesocket.session.SessionManager;
import com.li.gamesocket.session.SnCtx;
import com.li.gamesocket.session.SnCtxManager;
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
                log.debug("客户端ClientVocationalWorkHandler收到无效转发信息,忽略");
            }

            return;
        }

        Channel channel = null;
        if (snCtx.isForward()) {
            channel = snCtx.getChannel();
            Session session = channel.attr(ChannelAttributeKeys.SESSION).get();

            if (log.isDebugEnabled()) {
                log.debug("转发响应消息[{}]至[{}]", msg, session.ip());
            }

            IMessage message = MessageFactory.transformResponse(msg, session);

            sessionManager.writeAndFlush(session, message);

            return;
        }else {
            channel = ctx.channel();
        }

        NioNettyClient client = channel.attr(ChannelAttributeKeys.CLIENT).get();
        client.receive(msg);

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
