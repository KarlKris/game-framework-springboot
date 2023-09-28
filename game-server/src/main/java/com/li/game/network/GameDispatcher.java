package com.li.game.network;

import com.li.common.concurrent.IdentityThreadFactoryExecutor;
import com.li.engine.service.handler.AbstractDispatcher;
import com.li.engine.service.session.SessionManager;
import com.li.network.message.InnerMessage;
import com.li.network.message.ProtocolConstant;
import com.li.network.message.SocketProtocol;
import com.li.network.session.ISession;
import com.li.network.session.ServerSession;
import com.li.protocol.game.login.protocol.GameServerLoginModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * 消息分发处理器
 * @author li-yuanwen
 * @date 2021/12/13
 */
@Slf4j
@Component
public class GameDispatcher extends AbstractDispatcher<InnerMessage, ServerSession> {

    @Resource
    private SessionManager sessionManager;
    @Resource
    private IdentityThreadFactoryExecutor identityThreadFactoryExecutor;

    @Override
    protected long getProtocolIdentity(ServerSession session, InnerMessage message) {
        return message.getIdentity();
    }

    @Override
    protected Executor getExecutor(InnerMessage message, ServerSession session) {
        long identity = getProtocolIdentity(session, message);
        if (identity > 0) {
            return identityThreadFactoryExecutor.getExecutor(identity);
        }
        return identityThreadFactoryExecutor.next();
    }

    @Override
    protected void close() {
        try {
            identityThreadFactoryExecutor.shutdownGracefully().get();
            log.warn("关闭业务线程池");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
