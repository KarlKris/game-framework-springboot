package com.li.engine.service.rpc.invocation;

import com.li.engine.protocol.MessageFactory;
import com.li.engine.service.session.SessionManager;
import com.li.network.message.InnerMessage;
import com.li.network.message.OuterMessage;
import com.li.network.session.ISession;
import lombok.extern.slf4j.Slf4j;

/**
 * 转发消息Future
 * @author li-yuanwen
 * @date 2021/12/10
 */
@Slf4j
public class ForwardInvocation extends Invocation {

    /** 源目标 **/
    private final ISession session;
    /** 消息工厂 **/
    private final MessageFactory messageFactory;

    public ForwardInvocation(long sn, Long parentSn, long identity, ISession session, MessageFactory messageFactory) {
        super(sn, parentSn, identity, false);
        this.session = session;
        this.messageFactory = messageFactory;
    }

    @Override
    public void complete(InnerMessage message) {
        if (log.isDebugEnabled()) {
            log.debug("转发响应消息[{}]至[{}]", message.getSn(), session.getIp());
        }

        OuterMessage outerMessage = messageFactory.convertToResponseOuterMessage(getParentSn(), message, session);

        SessionManager.writeAndFlush(session, outerMessage);
    }
}
