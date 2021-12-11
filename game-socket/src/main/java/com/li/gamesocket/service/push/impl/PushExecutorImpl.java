package com.li.gamesocket.service.push.impl;

import cn.hutool.core.util.ZipUtil;
import com.li.gamesocket.channelhandler.common.ChannelAttributeKeys;
import com.li.gamesocket.protocol.*;
import com.li.gamesocket.protocol.serialize.SerializeType;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerHolder;
import com.li.gamesocket.service.VocationalWorkConfig;
import com.li.gamesocket.service.handler.ThreadSessionIdentityHolder;
import com.li.gamesocket.service.protocol.SocketProtocol;
import com.li.gamesocket.service.push.IPushExecutor;
import com.li.gamesocket.service.session.ISession;
import com.li.gamesocket.service.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    private VocationalWorkConfig vocationalWorkConfig;
    @Resource
    private SessionManager sessionManager;


    @Override
    public void pushToOuter(PushResponse pushResponse, SocketProtocol protocol) {
        byte[] body = pushResponse.getContent();
        boolean zip = false;
        if (body.length > vocationalWorkConfig.getBodyZipLength()) {
            body = ZipUtil.gzip(body);
            zip = true;
        }

        Serializer serializer;
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
            if (!Objects.equals(type, serializerHolder.getDefaultSerializer().getSerializerType())) {
                serializerHolder.getDefaultSerializer();
                serializer = serializerHolder.getSerializer(type);
                body = serializer.serialize(response);
                if (body.length > vocationalWorkConfig.getBodyZipLength()) {
                    body = ZipUtil.gzip(body);
                    zip = true;
                }
            }

            IMessage message = MessageFactory.toOuterMessage(0
                    , ProtocolConstant.VOCATIONAL_WORK_RES
                    , socketProtocol
                    , serializeType
                    , zip
                    , body);

            if (log.isDebugEnabled()) {
                log.debug("推送消息至外网[{},{}-{}]", message.getSn()
                        , message.getProtocol().getModule()
                        , message.getProtocol().getInstruction());
            }

            SessionManager.writeAndFlush(session, message);
        }
    }


    @Override
    public void pushToInner(ISession session, PushResponse pushResponse, SocketProtocol protocol) {
        Serializer defaultSerializer = serializerHolder.getDefaultSerializer();
        byte[] body = defaultSerializer.serialize(pushResponse);
        boolean zip = false;
        if (body.length > vocationalWorkConfig.getBodyZipLength()) {
            body = ZipUtil.gzip(body);
            zip = true;
        }

        InnerMessage message = MessageFactory.toInnerMessage(0L
                , ProtocolConstant.VOCATIONAL_WORK_RES
                , protocol
                , defaultSerializer.getSerializerType()
                , zip
                , body
                , ThreadSessionIdentityHolder.getIdentity()
                , session.getIp());

        if (log.isDebugEnabled()) {
            log.debug("推送消息至内网[{},{}-{}]", message.getSn()
                    , message.getProtocol().getModule()
                    , message.getProtocol().getMethodId());
        }

        SessionManager.writeAndFlush(session, message);
    }


}
