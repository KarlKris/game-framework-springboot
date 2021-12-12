package com.li.gamesocket.protocol;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ZipUtil;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerHolder;
import com.li.gamesocket.service.VocationalWorkConfig;
import com.li.gamesocket.service.protocol.MethodCtx;
import com.li.gamesocket.service.protocol.MethodParameter;
import com.li.gamesocket.service.protocol.SocketProtocol;
import com.li.gamesocket.service.protocol.SocketProtocolManager;
import com.li.gamesocket.service.protocol.impl.InBodyMethodParameter;
import com.li.gamesocket.service.rpc.SocketFutureManager;
import com.li.gamesocket.service.session.ISession;
import com.li.gamesocket.service.session.PlayerSession;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 * 消息工厂
 * (推送,应答前只能通过工厂构建消息,不允许私自调用对应消息的构造方法或静态创建方法)
 */
@Component
public class MessageFactory {

    @Resource
    private SerializerHolder serializerHolder;
    @Resource
    private SocketFutureManager socketFutureManager;
    @Resource
    private SocketProtocolManager socketProtocolManager;
    @Resource
    private VocationalWorkConfig config;

    public InnerMessage convertToInnerMessage(OuterMessage outerMessage, PlayerSession playerSession) {

        byte type = outerMessage.getMessageType();
        byte[] body = outerMessage.getBody();

        if (ArrayUtil.isNotEmpty(body) && type != SerializerHolder.DEFAULT_SERIALIZER.getSerializerType()) {
            Serializer originSerializer = serializerHolder.getSerializer(type);
            type = SerializerHolder.DEFAULT_SERIALIZER.getSerializerType();

            MethodCtx methodCtx = socketProtocolManager.getMethodCtxBySocketProtocol(outerMessage.getProtocol());
            for (MethodParameter methodParameter : methodCtx.getParams()) {
                if (methodParameter instanceof InBodyMethodParameter) {
                    Object param = originSerializer.deserialize(body, methodParameter.getParameterClass());
                    body = SerializerHolder.DEFAULT_SERIALIZER.serialize(param);
                }
            }

        }

        long nextSn = socketFutureManager.nextSn();
        return toInnerMessage(nextSn
                , outerMessage.getMessageType()
                , outerMessage.getProtocol()
                , type
                , body
                , playerSession.getIdentity()
                , playerSession.getIp());
    }


    public OuterMessage convertToOuterMessage(InnerMessage message, ISession session) {
        return null;
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
        byte[] ipBytes = StringUtils.isEmpty(ip) ? null : ip.getBytes();
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
