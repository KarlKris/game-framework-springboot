package com.li.gamesocket.service.rpc.future;

import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.protocol.MessageFactory;
import com.li.gamesocket.protocol.ProtocolConstant;
import com.li.gamesocket.service.session.ISession;
import com.li.gamesocket.service.session.SessionManager;
import lombok.extern.slf4j.Slf4j;

/**
 * 转发消息Future
 * @author li-yuanwen
 * @date 2021/12/10
 */
@Slf4j
public class ForwardSocketFuture extends SocketFuture {

    /** 请求消息序号 **/
    private final long outerSn;
    /** 源目标 **/
    private final ISession session;

    public ForwardSocketFuture(long sn, long outerSn, ISession session) {
        super(sn);
        this.outerSn = outerSn;
        this.session = session;
    }

    @Override
    public void complete(IMessage message) {
        if (log.isDebugEnabled()) {
            log.debug("转发响应消息[{}]至[{}]", message.getSn(), session.getIp());
        }

        Byte serializeType = session.getSerializeType();
        boolean zip = message.zip();
        byte[] body = message.getBody();
        if (serializeType != message.getSerializeType()) {
            // 理论上不会执行这里
            log.warn("转发响应消息时发现序列化方式不一致,仔细检查转发逻辑");
        }

        IMessage msg = MessageFactory.toOuterMessage(outerSn
                , ProtocolConstant.toOriginMessageType(message.getMessageType())
                , message.getProtocol()
                , serializeType
                , zip
                , body);

        SessionManager.writeAndFlush(session, msg);
    }
}
