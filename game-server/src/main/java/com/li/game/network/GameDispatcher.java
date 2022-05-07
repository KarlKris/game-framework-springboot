package com.li.game.network;

import com.li.engine.service.handler.AbstractDispatcher;
import com.li.engine.service.handler.ThreadSessionIdentityHolder;
import com.li.engine.service.session.SessionManager;
import com.li.network.message.InnerMessage;
import com.li.network.message.ProtocolConstant;
import com.li.network.message.SocketProtocol;
import com.li.network.session.ISession;
import com.li.network.session.ServerSession;
import com.li.protocol.game.login.protocol.GameServerLoginModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 消息分发处理器
 * @author li-yuanwen
 * @date 2021/12/13
 */
@Component
public class GameDispatcher extends AbstractDispatcher<InnerMessage, ServerSession> {

    @Resource
    private SessionManager sessionManager;

    @Override
    protected void setIdentityToThreadLocal(ServerSession session, InnerMessage message) {
        ThreadSessionIdentityHolder.setIdentity(message.getMessageType());
    }

    @Override
    protected long getProtocolIdentity(ServerSession session, InnerMessage message) {
        return message.getIdentity();
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
    protected long getIdBySessionAndMessage(ServerSession session, InnerMessage message) {
        long id = session.getSessionId();
        if (message.getIdentity() > 0) {
            id = message.getIdentity();
        }
        return id;
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
