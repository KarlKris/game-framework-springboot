package com.li.gamegateway.network;

import com.li.gamesocket.channelhandler.common.ChannelAttributeKeys;
import com.li.gamesocket.channelhandler.server.AbstractServerVocationalWorkHandler;
import com.li.gamesocket.protocol.OuterMessage;
import com.li.gamesocket.service.session.PlayerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 网关服业务逻辑处理Handler
 * @author li-yuanwen
 * @date 2021/12/8
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class GatewayVocationalWorkHandler extends AbstractServerVocationalWorkHandler<OuterMessage, PlayerSession> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, OuterMessage outerMessage) throws Exception {
        PlayerSession playerSession = (PlayerSession) ctx.channel().attr(ChannelAttributeKeys.SESSION).get();
        dispatcher.dispatch(outerMessage, playerSession);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        PlayerSession playerSession = sessionManager.registerPlayerSession(ctx.channel());

        if (log.isDebugEnabled()) {
            log.debug("与客户端[{}]建立连接,注册PlayerSession[{}]", playerSession.getIp(), playerSession.getSessionId());
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        PlayerSession playerSession = sessionManager.removePlayerSession(ctx.channel());

        if (log.isDebugEnabled() && playerSession != null) {
            log.debug("与客户端[{}]断开连接,注册PlayerSession[{}]", playerSession.getIp(), playerSession.getSessionId());
        }

        super.channelInactive(ctx);
    }
}
