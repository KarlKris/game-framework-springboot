package com.li.gameserver.network;

import com.li.network.modules.ErrorCodeModule;
import com.li.network.message.InnerMessage;
import com.li.network.message.ProtocolConstant;
import com.li.engine.service.handler.AbstractDispatcher;
import com.li.engine.service.handler.ThreadSessionIdentityHolder;
import com.li.network.message.SocketProtocol;
import com.li.network.session.ServerSession;
import com.li.engine.service.session.SessionManager;
import org.springframework.stereotype.Component;

/**
 * 游戏服消息分发处理器
 * @author li-yuanwen
 * @date 2021/12/13
 */
@Component
public class GameDispatcher extends AbstractDispatcher<InnerMessage, ServerSession> {

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
    protected SocketProtocol errorSocketProtocol() {
        return errorProtocol;
    }

    private final SocketProtocol errorProtocol = new SocketProtocol(ErrorCodeModule.MODULE, ErrorCodeModule.ERROR_CODE);

    @Override
    protected long getIdBySessionAndMessage(ServerSession session, InnerMessage message) {
        long id = session.getSessionId();
        if (message.getIdentity() > 0) {
            id = message.getIdentity();
        }
        return id;
    }
}
