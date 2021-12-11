package com.li.gamegateway.network;

import com.li.gamecommon.rpc.RemoteServerSeekService;
import com.li.gamecommon.rpc.model.Address;
import com.li.gamesocket.client.NioNettyClient;
import com.li.gamesocket.client.NioNettyClientFactory;
import com.li.gamesocket.protocol.*;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.service.handler.ThreadSessionIdentityHolder;
import com.li.gamesocket.service.protocol.MethodCtx;
import com.li.gamesocket.service.protocol.MethodInvokeCtx;
import com.li.gamesocket.service.protocol.MethodParameter;
import com.li.gamesocket.service.protocol.impl.IdentityMethodParameter;
import com.li.gamesocket.service.protocol.impl.SessionMethodParameter;
import com.li.gamesocket.service.handler.AbstractDispatcher;
import com.li.gamesocket.service.rpc.SocketFutureManager;
import com.li.gamesocket.service.rpc.future.ForwardSocketFuture;
import com.li.gamesocket.service.session.PlayerSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

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

    @Override
    public void dispatch(OuterMessage message, PlayerSession session) {
        if (!message.isRequest()) {
            if (log.isDebugEnabled()) {
                log.debug("服务器收到响应消息,忽略");
            }
            return;
        }
        // 交付给线程池执行
        execute(session, () -> dispatch0(session, message));
    }

    @Override
    public void execute(PlayerSession session, Runnable runnable) {
        long id = session.getSessionId();
        if (session.isIdentity()) {
            id = session.getIdentity();
        }

        // 提交任务
        submit(id, runnable);
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

        NioNettyClient client = clientFactory.connectTo(address);

        long nextSn = socketFutureManager.nextSn();
        // 构建内部消息进行转发
        InnerMessage innerMessage = MessageFactory.toInnerMessage(nextSn
                , ProtocolConstant.toOriginMessageType(message.getMessageType())
                , message.getProtocol()
                , message.getSerializeType()
                , message.zip()
                , message.getBody()
                , session.getIdentity()
                , null);

        try {
            client.send(innerMessage
                    , (msg, completableFuture)
                            -> socketFutureManager.addSocketFuture(new ForwardSocketFuture(nextSn, message.getSn(), session)));
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
    protected Object invokeMethod(PlayerSession session, OuterMessage message, MethodInvokeCtx methodInvokeCtx) {
        if (methodInvokeCtx.isIdentity() && !session.isIdentity()) {
            return Response.NO_IDENTITY;
        }

        Serializer serializer = serializerHolder.getSerializer(message.getSerializeType());

        MethodCtx methodCtx = methodInvokeCtx.getMethodCtx();
        MethodParameter[] params = methodCtx.getParams();
        Object[] args = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            MethodParameter parameters = params[i];
            if (parameters instanceof SessionMethodParameter) {
                args[i] = session;
                continue;
            }

            if (parameters instanceof IdentityMethodParameter) {
                args[i] = session.getIdentity();
                continue;
            }

            args[i] = serializer.deserialize(message.getBody(), parameters.getParameterClass());
        }

        return ReflectionUtils.invokeMethod(methodCtx.getMethod(), methodInvokeCtx.getTarget(), args);
    }

    @Override
    protected void response(PlayerSession session, OuterMessage message, boolean zip, byte serializeType, byte[] responseBody) {

    }

    @Override
    protected Object createErrorCodeBody(int errorCode) {
        return null;
    }
}
