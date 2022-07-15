package com.li.game.network;

import com.li.common.concurrency.MultiThreadRunnableLoopGroup;
import com.li.common.concurrency.RunnableLoopGroup;
import com.li.engine.service.handler.AbstractDispatcher;
import com.li.engine.service.session.SessionManager;
import com.li.network.message.InnerMessage;
import com.li.network.message.ProtocolConstant;
import com.li.network.message.SocketProtocol;
import com.li.network.session.ISession;
import com.li.network.session.PlayerSession;
import com.li.network.session.ServerSession;
import com.li.protocol.game.login.protocol.GameServerLoginModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * 消息分发处理器
 * @author li-yuanwen
 * @date 2021/12/13
 */
@Component
public class GameDispatcher extends AbstractDispatcher<InnerMessage, ServerSession> {

    @Resource
    private SessionManager sessionManager;

    // todo
    private final RunnableLoopGroup group = new MultiThreadRunnableLoopGroup();

    @Override
    protected long getProtocolIdentity(ServerSession session, InnerMessage message) {
        return message.getIdentity();
    }

    @Override
    protected Executor getExecutor(InnerMessage message, ServerSession session) {
        long identity = getProtocolIdentity(session, message);
        if (identity > 0) {
            PlayerSession playerSession = session.getPlayerSession(identity);
            if (playerSession != null) {
                if (!playerSession.isRegisterRunnableLoop()) {
                    group.register(playerSession);
                }
                return playerSession.runnableLoop();
            }
        }
        return group.next();
    }

    @Override
    protected void response(ServerSession session, InnerMessage message, SocketProtocol protocol, byte[] responseBody) {
        InnerMessage innerMessage = messageFactory.toInnerMessage(message.getSn()
                , ProtocolConstant.transformResponse(message.getMessageType())
                , protocol
                , message.getSerializeType()
                , responseBody
                , -1L
                , null);

        SessionManager.writeAndFlush(session, innerMessage);
    }


    @Override
    protected boolean beforeDispatch(ServerSession session, InnerMessage message) {
        // 放行登陆协议,登陆逻辑需实现挤人功能
        SocketProtocol protocol = message.getProtocol();
        if (protocol.getModule() == GameServerLoginModule.MODULE
                && protocol.getMethodId() == GameServerLoginModule.LOGIN) {
            return true;
        }

        ISession identitySession = sessionManager.getIdentitySession(message.getIdentity());
        // 重新绑定,处理网关服与游戏服重连的情况下,玩家Session信息丢失后重新绑定
        if (identitySession == null) {
            sessionManager.bindIdentity(session, message.getIdentity());
            return true;
        }

        return Objects.equals(session.getSessionId(), identitySession.getSessionId());
    }
}
