package com.li.game.network;

import com.li.engine.channelhandler.server.AbstractServerVocationalWorkHandler;
import com.li.engine.service.session.SessionManager;
import com.li.common.thread.SerializedExecutorService;
import com.li.network.message.InnerMessage;
import com.li.network.protocol.ChannelAttributeKeys;
import com.li.network.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 游戏服业务逻辑ChannelHandler
 * @author li-yuanwen
 * @date 2021/12/13
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class GameVocationalWorkHandler extends AbstractServerVocationalWorkHandler<InnerMessage, ServerSession> {

    @Resource
    private SessionManager sessionManager;
    @Resource
    private SerializedExecutorService executorService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InnerMessage innerMessage) throws Exception {
        ServerSession serverSession = (ServerSession) ctx.channel().attr(ChannelAttributeKeys.SESSION).get();
        dispatcher.dispatch(innerMessage, serverSession);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ServerSession serverSession = sessionManager.registerServerSession(ctx.channel());
        if (log.isDebugEnabled()) {
            log.debug("与客户端[{}]建立连接,注册PlayerSession[{}]", serverSession.getIp(), serverSession.getSessionId());
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ServerSession serverSession = sessionManager.removeServerSession(ctx.channel());

        if (serverSession != null) {
            if (log.isDebugEnabled()) {
                log.debug("与客户端[{}]断开连接,注册PlayerSession[{}]", serverSession.getIp(), serverSession.getSessionId());
            }

            for (long id : serverSession.getIdentities()) {
                sessionManager.logout(id);
                executorService.destroy(id);
            }
        }

        executorService.destroy(serverSession.getSessionId());

        super.channelInactive(ctx);
    }
}
