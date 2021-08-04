package com.li.gamesocket.protocol;

import com.li.gamesocket.session.Session;
import org.springframework.util.StringUtils;

/**
 * @author li-yuanwen
 * 消息工厂
 * (推送,应答前只能通过工厂构建消息,不允许私自调用对应消息的构造方法或静态创建方法)
 */
public class MessageFactory {

    /** 服务器内部心跳消息包 **/
    public static final InnerMessage HEART_BEAT_REQ_INNER_MSG = InnerMessage.of(
            InnerMessageHeader.of(ProtocolConstant.HEART_BEAT_REQ, null, false, 0, 0, null)
            , null);
    public static final InnerMessage HEART_BEAT_RES_INNER_MSG = InnerMessage.of(
            InnerMessageHeader.of(ProtocolConstant.HEART_BEAT_RES, null, false, 0, 0, null)
            , null);

    /** 服务器外部心跳消息包 **/
    public static final OuterMessage HEART_BEAT_REQ_OUTER_MSG = OuterMessage.of(
            OuterMessageHeader.of(0, ProtocolConstant.HEART_BEAT_REQ, null, false)
            , null);
    public static final OuterMessage HEART_BEAT_RES_OUTER_MSG = OuterMessage.of(
            OuterMessageHeader.of(0, ProtocolConstant.HEART_BEAT_RES, null, false)
            , null);


    /**
     * 根据请求消息和结果构建响应消息
     * @param message 请求消息
     * @param body 响应体
     * @param zip 是否压缩了响应体
     * @return 消息
     */
    public static IMessage transformResponseMsg(Session session, IMessage message, byte[] body, boolean zip) {
        if (message.isOuterMessage()) {
            return OuterMessage.of(
                    OuterMessageHeader.of(message.getSn()
                            , ProtocolConstant.transformResponse(message.getMessageType())
                            , message.getCommand()
                            , zip)
                    , body);
        }

        if (message.isInnerMessage()) {
            String ip = session.ip();
            byte[] ipBytes = StringUtils.isEmpty(ip) ? null : ip.getBytes();
            return InnerMessage.of(
                    InnerMessageHeader.of(ProtocolConstant.transformResponse(message.getMessageType())
                            , message.getCommand()
                            , zip
                            , message.getSn()
                            , session.getIdentity()
                            , ipBytes)
                    , body);
        }

        return null;
    }

    /**
     * 将请求消息转换成内部请求消息InnerMessage
     * @param message 请求消息
     * @param sn 内部序号
     * @param session 请求源
     * @return /
     */
    public static InnerMessage transformInnerRequest(IMessage message, long sn, Session session) {
        if (!message.isRequest()) {
            throw new IllegalArgumentException("message belong to response");
        }

        String ip = session.ip();
        byte[] ipBytes = StringUtils.isEmpty(ip) ? null : ip.getBytes();
        InnerMessageHeader header = InnerMessageHeader.of(message.getMessageType()
                , message.getCommand()
                , message.zip(), sn, session.getIdentity(), ipBytes);

        return InnerMessage.of(header, message.getBody());
    }

    /**
     * 将响应消息转换成外部响应消息
     * @return 外部响应消息
     */
    public static IMessage transformResponse(IMessage message, Session session) {
        return null;
    }


}
