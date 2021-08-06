package com.li.gamesocket.channelhandler.impl;

import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.service.handler.Dispatcher;
import com.li.gamesocket.service.session.Session;
import com.li.gamesocket.service.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author li-yuanwen
 * @date 2021/7/30 20:26
 * 业务逻辑ChannelHandler
 **/
@Slf4j
@Component
@ChannelHandler.Sharable
public class VocationalWorkHandler extends SimpleChannelInboundHandler<IMessage> {

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private Dispatcher dispatcher;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMessage msg) throws Exception {
        // 修改通信协议
        ctx.channel().attr(ChannelAttributeKeys.LAST_PROTOCOL_HEADER_IDENTITY).set(msg.getProtocolHeaderIdentity());

        Session session = ctx.channel().attr(ChannelAttributeKeys.SESSION).get();
        dispatcher.dispatch(msg, session);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Session session = sessionManager.registerSession(ctx.channel());

        if (log.isDebugEnabled()) {
            log.debug("与客户端[{}]建立连接,注册Session[{}]", session.getChannel().id(), session.getSessionId());
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = sessionManager.removeSession(ctx.channel());

        if (log.isDebugEnabled()) {
            log.debug("与客户端[{}]断开连接,移除失效Session[{}]", session.getChannel().id(), session.getSessionId());
        }

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.error("服务器发生IOException,与客户端[{}]断开连接", ctx.channel().id(), cause);
            ctx.close();
        }else {
            log.error("服务器发生未知异常", cause);
        }
    }
}
