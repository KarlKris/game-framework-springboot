package com.li.engine.service.push.impl;

import com.li.engine.protocol.MessageFactory;
import com.li.engine.service.push.IPushExecutor;
import com.li.engine.service.session.SessionManager;
import com.li.network.message.*;
import com.li.network.protocol.InBodyMethodParameter;
import com.li.network.protocol.MethodCtx;
import com.li.network.protocol.MethodParameter;
import com.li.network.protocol.SocketProtocolManager;
import com.li.network.serialize.Serializer;
import com.li.network.serialize.SerializerHolder;
import com.li.network.session.ISession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 推送服务
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
