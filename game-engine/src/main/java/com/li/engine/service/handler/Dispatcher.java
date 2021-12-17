package com.li.engine.service.handler;

import com.li.network.message.IMessage;
import com.li.network.session.ISession;

/**
 * @author li-yuanwen
 * @date 2021/7/31 15:40
 * 消息分发器接口
 **/
public interface Dispatcher<M extends IMessage, S extends ISession> {

    /**
     * 消息分发
     *
     * @param message 消息
     * @param session session
     */
    void dispatch(final M message,final S session);


}
