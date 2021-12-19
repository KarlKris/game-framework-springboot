package com.li.gamegateway.network;

import com.li.network.protocol.ChannelAttributeKeys;
import com.li.engine.channelhandler.server.AbstractServerVocationalWorkHandler;
import com.li.network.message.OuterMessage;
import com.li.engine.service.handler.DispatcherExecutorService;
import com.li.network.session.PlayerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 网关服业务逻辑处理Handler
 * @author li-yuanwen
 * @date 2021/12/8
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class GatewayVocationalWorkHandler extends AbstractServerVocationalWorkHandler<OuterMessage, PlayerSession> {

    @Resource
    private DispatcherExecutorService dispatcherExecutorService;

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

        if (playerSession != null) {
            long id = playerSession.getSessionId();
            if (playerSession.isIdentity()) {
                id = playerSession.getIdentity();
            }
            dispatcherExecutorService.execute(id, new Runnable() {
                @Override
                public void run() {
                    // todo 网关服需要考虑通知游戏服更新玩家断开链接
                }
            });
        }

        super.channelInactive(ctx);
    }
}
