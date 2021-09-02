package com.li.gamesocket.service.push.impl;

import cn.hutool.core.util.ZipUtil;
import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.protocol.*;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerManager;
import com.li.gamesocket.service.VocationalWorkConfig;
import com.li.gamesocket.service.command.CommandManager;
import com.li.gamesocket.service.command.MethodCtx;
import com.li.gamesocket.service.command.MethodInvokeCtx;
import com.li.gamesocket.service.handler.DispatcherExecutorService;
import com.li.gamesocket.service.push.PushProcessor;
import com.li.gamesocket.service.session.Session;
import com.li.gamesocket.service.session.SessionManager;
import com.li.gamesocket.utils.CommandUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

/**
 * @author li-yuanwen
 */
@Slf4j
@Component
public class PushProcessorImpl implements PushProcessor {

    @Autowired
    private CommandManager commandManager;
    @Autowired
    private SerializerManager serializerManager;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private VocationalWorkConfig vocationalWorkConfig;
    @Autowired
    private DispatcherExecutorService dispatcherExecutorService;

    @Override
    public void process(IMessage message) {
        dispatcherExecutorService.execute(() -> {
            MethodInvokeCtx methodInvokeCtx = commandManager.getMethodInvokeCtx(message.getCommand());
            if (methodInvokeCtx == null) {
                doPush(message);
                return;
            }

            // 查询序列化/反序列化方式
            byte serializeType = message.getSerializeType();
            Serializer serializer = serializerManager.getSerializer(serializeType);
            if (serializer == null) {
                if (log.isWarnEnabled()) {
                    log.warn("推送消息序列化类型[{}],找不到对应的序列化工具,忽略", serializeType);
                }
                return;
            }

            PushResponse pushResponse = serializer.deserialize(message.getBody(), PushResponse.class);
            MethodCtx methodCtx = methodInvokeCtx.getMethodCtx();

            Object[] args = CommandUtils.decodePushResponse(methodCtx.getParams(), pushResponse);
            ReflectionUtils.invokeMethod(methodCtx.getMethod(), methodInvokeCtx.getTarget(), args);
        });
    }


    /** 直接推送给目标 **/
    private void doPush(IMessage msg) {
        Serializer serializer = serializerManager.getSerializer(msg.getSerializeType());
        PushResponse pushResponse = serializer.deserialize(msg.getBody(), PushResponse.class);

        Response response = Response.SUCCESS(pushResponse.getContent());

        Byte serializeType = null;
        byte[] body = null;
        boolean zip = false;

        for (long identity : pushResponse.getTargets()) {
            Session session = sessionManager.getIdentitySession(identity);
            if (session == null) {
                continue;
            }

            Byte type = session.getChannel().attr(ChannelAttributeKeys.LAST_SERIALIZE_TYPE).get();
            if (!Objects.equals(type, serializeType)) {
                serializeType = type;
                serializer = serializerManager.getSerializer(type);
                body = serializer.serialize(response);
                if (body.length > vocationalWorkConfig.getBodyZipLength()) {
                    body = ZipUtil.gzip(body);
                    zip = true;
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("向玩家[{}]推送消息", identity);
            }

            Short lastProtocolHeaderIdentity = session.getChannel().attr(ChannelAttributeKeys.LAST_PROTOCOL_HEADER_IDENTITY).get();
            if (lastProtocolHeaderIdentity == null) {
                if (log.isDebugEnabled()) {
                    log.debug("未知玩家[{}]Channel使用的消息类型,忽略本次推送", identity);
                }
                continue;
            }

            IMessage message = null;
            if (lastProtocolHeaderIdentity == ProtocolConstant.PROTOCOL_INNER_HEADER_IDENTITY) {
                // 内部通信类型
                message = MessageFactory.toInnerMessage(msg.getSn()
                        , ProtocolConstant.toOriginMessageType(msg.getMessageType())
                        , msg.getCommand()
                        , serializeType
                        , zip
                        , body
                        , session.ip());
            } else if (lastProtocolHeaderIdentity == ProtocolConstant.PROTOCOL_OUTER_HEADER_IDENTITY) {
                // 外部通信类型
                message = MessageFactory.toOuterMessage(msg.getSn()
                        , ProtocolConstant.toOriginMessageType(msg.getMessageType())
                        , msg.getCommand()
                        , serializeType
                        , zip
                        , body);
            }

            sessionManager.writeAndFlush(session, message);
        }
    }

}
