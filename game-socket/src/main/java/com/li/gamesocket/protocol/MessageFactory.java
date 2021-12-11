package com.li.gamesocket.protocol;

import com.li.gamesocket.protocol.serialize.SerializeType;
import com.li.gamesocket.service.protocol.SocketProtocol;
import org.springframework.util.StringUtils;

/**
 * @author li-yuanwen
 * 消息工厂
 * (推送,应答前只能通过工厂构建消息,不允许私自调用对应消息的构造方法或静态创建方法)
 */
public class MessageFactory {

    /** 服务器内部心跳消息包 **/
    public static final InnerMessage HEART_BEAT_REQ_INNER_MSG = InnerMessage.of(
            InnerMessageHeader.of(ProtocolConstant.HEART_BEAT_REQ, null, false, SerializeType.PROTO_STUFF.getType(),  0, -1, null)
            , null);
    public static final InnerMessage HEART_BEAT_RES_INNER_MSG = InnerMessage.of(
            InnerMessageHeader.of(ProtocolConstant.HEART_BEAT_RES, null, false, SerializeType.PROTO_STUFF.getType(),  0, -1, null)
            , null);

    /** 服务器外部心跳消息包 **/
    public static final OuterMessage HEART_BEAT_REQ_OUTER_MSG = OuterMessage.of(
            OuterMessageHeader.of(0, ProtocolConstant.HEART_BEAT_REQ, null, false, SerializeType.JSON.getType())
            , null);
    public static final OuterMessage HEART_BEAT_RES_OUTER_MSG = OuterMessage.of(
            OuterMessageHeader.of(0, ProtocolConstant.HEART_BEAT_RES, null, false, SerializeType.JSON.getType())
            , null);



    /**
     * 构建内部消息
     * @param sn 消息序号
     * @param type 消息类型
     * @param protocol 协议
     * @param serializeType 序列化类型
     * @param zip 消息体是否压缩
     * @param body 消息体
     * @param ip ip
     * @return 内部消息
     */
    public static InnerMessage toInnerMessage(long sn, byte type, SocketProtocol protocol
            , byte serializeType, boolean zip, byte[] body, long identity, String ip) {
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
     * @param zip 消息体是否压缩
     * @param body 消息体
     * @return 外部消息
     */
    public static OuterMessage toOuterMessage(long sn, byte type, SocketProtocol protocol
            , byte serializeType, boolean zip, byte[] body) {
        OuterMessageHeader header = OuterMessageHeader.of(sn, type, protocol, zip, serializeType);
        return OuterMessage.of(header, body);
    }


}
