package com.li.gamesocket.service.push.impl;

import com.li.gamesocket.protocol.*;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerHolder;
import com.li.gamesocket.service.protocol.MethodCtx;
import com.li.gamesocket.service.protocol.MethodParameter;
import com.li.gamesocket.service.protocol.SocketProtocol;
import com.li.gamesocket.service.protocol.SocketProtocolManager;
import com.li.gamesocket.service.protocol.impl.InBodyMethodParameter;
import com.li.gamesocket.service.push.IPushExecutor;
import com.li.gamesocket.service.session.ISession;
import com.li.gamesocket.service.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author li-yuanwen
 */
@Slf4j
@Component
public class PushExecutorImpl implements IPushExecutor {


    @Resource
    private SerializerHolder serializerHolder;
    @Resource
    private SessionManager sessionManager;
    @Resource
    private MessageFactory messageFactory;
    @Resource
    private SocketProtocolManager socketProtocolManager;


    @Override
    public void pushToOuter(PushResponse pushResponse, SocketProtocol protocol) {
        byte[] content = pushResponse.getContent();

        Map<Byte, byte[]> serializeType2body = new HashMap<>(2);
        for (long identity : pushResponse.getTargets()) {
            ISession session = sessionManager.getIdentitySession(identity);
            if (session == null) {
                continue;
            }

            // 客户端未主动通讯过忽略,理论上不会未null
            Byte type = session.getSerializeType();
            if (type == null) {
                continue;
            }

            byte[] body = serializeType2body.get(type);
            if (body == null) {
                if (!Objects.equals(type, SerializerHolder.DEFAULT_SERIALIZER.getSerializerType())) {
                    MethodCtx ctx = socketProtocolManager.getMethodCtxBySocketProtocol(protocol);
                    for (MethodParameter parameter : ctx.getParams()) {
                        if (parameter instanceof InBodyMethodParameter) {
                            Object obj = SerializerHolder.DEFAULT_SERIALIZER.deserialize(content, parameter.getParameterClass());
                            Serializer serializer = serializerHolder.getSerializer(type);
                            body = serializer.serialize(obj);
                        }
                    }
                } else {
                    body = content;
                }

                serializeType2body.put(type, body);
            }

            OuterMessage outerMessage = messageFactory.toOuterMessage(0L
                    , ProtocolConstant.VOCATIONAL_WORK_RES
                    , protocol
                    , type
                    , body);

            if (log.isDebugEnabled()) {
                log.debug("推送消息至外网[{},{}]", outerMessage.getSn(), outerMessage.getProtocol());
            }

            SessionManager.writeAndFlush(session, outerMessage);
        }
    }


    @Override
    public void pushToInner(ISession session, PushResponse pushResponse, SocketProtocol protocol) {

        byte[] body = SerializerHolder.DEFAULT_SERIALIZER.serialize(pushResponse);
        InnerMessage message = messageFactory.toInnerMessage(0L
                , ProtocolConstant.VOCATIONAL_WORK_RES
                , protocol
                , SerializerHolder.DEFAULT_SERIALIZER.getSerializerType()
                , body
                , -1L
                , session.getIp());

        if (log.isDebugEnabled()) {
            log.debug("推送消息至内网[{},{}-{}]", message.getSn()
                    , message.getProtocol().getModule()
                    , message.getProtocol().getMethodId());
        }

        SessionManager.writeAndFlush(session, message);
    }


}
