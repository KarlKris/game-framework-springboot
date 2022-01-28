package com.li.gamegateway.network;

import com.li.engine.client.NioNettyClient;
import com.li.engine.client.NioNettyClientFactory;
import com.li.engine.protocol.MessageFactory;
import com.li.engine.service.handler.AbstractDispatcher;
import com.li.engine.service.handler.ThreadSessionIdentityHolder;
import com.li.engine.service.rpc.SocketFutureManager;
import com.li.engine.service.rpc.future.ForwardSocketFuture;
import com.li.engine.service.session.SessionManager;
import com.li.gamecommon.rpc.RemoteServerSeekService;
import com.li.gamecommon.rpc.model.Address;
import com.li.network.message.InnerMessage;
import com.li.network.message.OuterMessage;
import com.li.network.message.ProtocolConstant;
import com.li.network.message.SocketProtocol;
import com.li.network.session.PlayerSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 网关服消息分发处理器
 * @author li-yuanwen
 * @date 2021/12/9
 */
@Slf4j
@Component
public class GatewayDispatcher extends AbstractDispatcher<OuterMessage, PlayerSession> {

    @Resource
    private RemoteServerSeekService remoteServerSeekService;
    @Resource
    private NioNettyClientFactory clientFactory;
    @Resource
    private SocketFutureManager socketFutureManager;
    @Resource
    private MessageFactory messageFactory;

    @Override
    protected long getIdBySessionAndMessage(PlayerSession session, OuterMessage message) {
        long id = session.getSessionId();
        if (session.isIdentity()) {
            id = session.getIdentity();
        }
        return id;
    }

    @Override
    protected boolean forwardMessage(PlayerSession session, OuterMessage message) {
        if (!session.isIdentity()) {
            if (log.isDebugEnabled()) {
                log.debug("连接Session[{}]未绑定身份标识,忽略本次转发", session.getSessionId());
            }
            return false;
        }

        Address address = remoteServerSeekService.seekApplicationAddressByModule(message.getProtocol().getModule()
                , session.getIdentity());

        if (address == null) {
            if (log.isDebugEnabled()) {
                log.debug("模块号[{}],身份标识[{}]无法找到目标服务器,忽略本次转发"
                        , message.getProtocol().getModule()
                        , session.getIdentity());
            }

            return false;
        }

        InnerMessage innerMessage = messageFactory.convertToInnerMessage(message, session);

        NioNettyClient client = clientFactory.connectTo(address);

        try {
            client.send(innerMessage
                    , (msg, completableFuture)
                            -> socketFutureManager.addSocketFuture(new ForwardSocketFuture(msg.getSn()
                            , message.getSn(), session)));
            return true;
        } catch (InterruptedException e) {
            log.error("消息转发至[{}]发生未知异常", address, e);
            return false;
        }
    }

    @Override
    protected void setIdentityToThreadLocal(PlayerSession session, OuterMessage message) {
        ThreadSessionIdentityHolder.setIdentity(session.getIdentity());
    }

    @Override
    protected long getProtocolIdentity(PlayerSession session, OuterMessage message) {
        return session.getIdentity();
    }

    @Override
    protected void response(PlayerSession session, OuterMessage message, SocketProtocol protocol, byte[] responseBody) {
        OuterMessage outerMessage = messageFactory.toOuterMessage(message.getSn()
                , ProtocolConstant.transformResponse(message.getMessageType())
                , protocol
                , message.getSerializeType()
                , responseBody);

        SessionManager.writeAndFlush(session, outerMessage);
    }
}
