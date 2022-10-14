package com.li.gateway.network;

import com.li.common.concurrent.RunnableLoopGroup;
import com.li.common.rpc.RemoteServerSeekService;
import com.li.common.rpc.model.Address;
import com.li.engine.client.NettyClient;
import com.li.engine.client.NioNettyClientFactory;
import com.li.engine.protocol.MessageFactory;
import com.li.engine.service.handler.AbstractDispatcher;
import com.li.engine.service.push.ResponseMessagePushProcessor;
import com.li.engine.service.rpc.InvocationManager;
import com.li.engine.service.rpc.invocation.ForwardInvocation;
import com.li.engine.service.session.SessionManager;
import com.li.network.message.InnerMessage;
import com.li.network.message.OuterMessage;
import com.li.network.message.ProtocolConstant;
import com.li.network.message.SocketProtocol;
import com.li.network.serialize.Serializer;
import com.li.network.session.PlayerSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executor;


/**
 * 网关服消息分发处理器
 * @author li-yuanwen
 * @date 2021/12/9
 */
@Slf4j
@Component
public class GatewayDispatcher extends AbstractDispatcher<OuterMessage, PlayerSession>
        implements ResponseMessagePushProcessor<PlayerSession> {

    @Resource
    private RemoteServerSeekService remoteServerSeekService;
    @Resource
    private NioNettyClientFactory clientFactory;
    @Resource
    private InvocationManager invocationManager;
    @Resource
    private MessageFactory messageFactory;
    @Resource
    private RunnableLoopGroup group;

    @Override
    protected Executor getExecutor(OuterMessage message, PlayerSession session) {
        if (session.isIdentity()) {
            if (!session.isRegisterRunnableLoop()) {
                group.register(session);
            }
            return session.runnableLoop();
        }
        return group.next();
    }

    @Override
    protected void close() {
        try {
            group.shutdownGracefully().get();
            log.warn("关闭业务线程池");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        long nextSn = invocationManager.nextSn();
        InnerMessage innerMessage = messageFactory.convertToRequestInnerMessage(nextSn, message, session);
        final long identity = getProtocolIdentity(session, message);

        NettyClient client = clientFactory.newInstance(address);

        ForwardInvocation forwardInvocation = new ForwardInvocation(innerMessage.getSn()
                , message.getSn(), identity, session, messageFactory);

        try {
            client.send(innerMessage, forwardInvocation);
            return true;
        } catch (InterruptedException e) {
            log.error("消息转发至[{}]发生未知异常", address, e);
            return false;
        }
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

    @Override
    public void response(PlayerSession session, long messageSn, SocketProtocol protocol, Object responseBody) {
        Byte serializeType = session.getSerializeType();

        byte[] body = null;
        if (responseBody != null) {
            Serializer serializer = serializerHolder.getSerializer(serializeType);
            body = serializer.serialize(responseBody);
        }

        OuterMessage message = messageFactory.toOuterMessage(messageSn, ProtocolConstant.VOCATIONAL_WORK_RES
                , protocol, serializeType, body);
        SessionManager.writeAndFlush(session, message);
    }
}
