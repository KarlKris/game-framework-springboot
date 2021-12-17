package com.li.engine.service.rpc.future;

import com.li.gamecommon.ApplicationContextHolder;
import com.li.network.message.InnerMessage;
import com.li.engine.protocol.MessageFactory;
import com.li.network.message.OuterMessage;
import com.li.network.session.ISession;
import com.li.engine.service.session.SessionManager;
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
    public void complete(InnerMessage message) {
        if (log.isDebugEnabled()) {
            log.debug("转发响应消息[{}]至[{}]", message.getSn(), session.getIp());
        }

        OuterMessage outerMessage = ApplicationContextHolder.getBean(MessageFactory.class)
                .convertToOuterMessage(message, session);

        SessionManager.writeAndFlush(session, outerMessage);
    }
}
