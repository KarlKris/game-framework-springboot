package com.li.engine.protocol;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ZipUtil;
import com.li.engine.service.VocationalWorkConfig;
import com.li.network.message.*;
import com.li.network.protocol.InBodyMethodParameter;
import com.li.network.protocol.MethodParameter;
import com.li.network.protocol.ProtocolMethodCtx;
import com.li.network.protocol.SocketProtocolManager;
import com.li.network.serialize.Serializer;
import com.li.network.serialize.SerializerHolder;
import com.li.network.session.ISession;
import com.li.network.session.PlayerSession;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 消息工厂
 * 推送,应答前只能通过工厂构建消息,不允许私自调用对应消息的构造方法或静态创建方法
 * @author li-yuanwen
 */
@Component
public class MessageFactory {

    @Resource
    private SerializerHolder serializerHolder;
    @Resource
    private SocketProtocolManager socketProtocolManager;
    @Resource
    private VocationalWorkConfig config;

    public InnerMessage convertToRequestInnerMessage(long sn, OuterMessage outerMessage, PlayerSession playerSession) {

        byte serializeType = outerMessage.getSerializeType();
        byte[] body = outerMessage.getBody();

        if (ArrayUtil.isNotEmpty(body) && serializeType != SerializerHolder.DEFAULT_SERIALIZER.getSerializerType()) {
            Serializer originSerializer = serializerHolder.getSerializer(serializeType);
            serializeType = SerializerHolder.DEFAULT_SERIALIZER.getSerializerType();

            ProtocolMethodCtx protocolMethodCtx = socketProtocolManager.getMethodCtxBySocketProtocol(outerMessage.getProtocol());
            for (MethodParameter methodParameter : protocolMethodCtx.getParams()) {
                if (methodParameter instanceof InBodyMethodParameter) {
                    Object param = originSerializer.deserialize(body, methodParameter.getParameterClass());
                    body = SerializerHolder.DEFAULT_SERIALIZER.serialize(param);
                }
            }

        }
        return toInnerMessage(sn
                , outerMessage.getMessageType()
                , outerMessage.getProtocol()
                , serializeType
                , body
                , playerSession.getIdentity()
                , playerSession.getIp());
    }


    public OuterMessage convertToResponseOuterMessage(long sn, InnerMessage innerMessage, ISession session) {

        byte serializeType = innerMessage.getSerializeType();
        byte[] body = innerMessage.getBody();

        if (ArrayUtil.isNotEmpty(body) && serializeType != session.getSerializeType()) {
            Serializer originSerializer = serializerHolder.getSerializer(serializeType);

            ProtocolMethodCtx protocolMethodCtx = socketProtocolManager.getMethodCtxBySocketProtocol(innerMessage.getProtocol());
            Object returnObj = originSerializer.deserialize(body, protocolMethodCtx.getReturnClz());

            Serializer serializer = serializerHolder.getSerializer(session.getSerializeType());
            body = serializer.serialize(returnObj);

        }

        return toOuterMessage(sn
                , innerMessage.getMessageType()
                , innerMessage.getProtocol()
                , session.getSerializeType()
                , body);

    }


    /**
     * 构建内部消息
     * @param sn 消息序号
     * @param type 消息类型
     * @param protocol 协议
     * @param serializeType 序列化类型
     * @param body 消息体
     * @param ip ip
     * @return 内部消息
     */
    public InnerMessage toInnerMessage(long sn, byte type, SocketProtocol protocol
            , byte serializeType, byte[] body, long identity, String ip) {
        boolean zip = false;
        if (ArrayUtil.isNotEmpty(body) && body.length > config.getBodyZipLength()) {
            body = ZipUtil.gzip(body);
            zip = true;
        }
        byte[] ipBytes = StringUtils.hasLength(ip) ? ip.getBytes() : null;
        InnerMessageHeader header = InnerMessageHeader.of(type, protocol, zip, serializeType, sn, identity, ipBytes);
        return InnerMessage.of(header, body);
    }



    /**
     * 构建外部消息
     * @param sn 消息序号
     * @param type 消息类型
     * @param protocol 协议
     * @param serializeType 序列化类型
     * @param body 消息体
     * @return 外部消息
     */
    public OuterMessage toOuterMessage(long sn, byte type, SocketProtocol protocol
            , byte serializeType, byte[] body) {
        boolean zip = false;
        if (ArrayUtil.isNotEmpty(body) && body.length > config.getBodyZipLength()) {
            body = ZipUtil.gzip(body);
            zip = true;
        }
        OuterMessageHeader header = OuterMessageHeader.of(sn, type, protocol, zip, serializeType);
        return OuterMessage.of(header, body);
    }




}
