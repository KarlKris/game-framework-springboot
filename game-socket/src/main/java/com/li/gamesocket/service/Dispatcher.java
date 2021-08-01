package com.li.gamesocket.service;

import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.session.Session;

/**
 * @author li-yuanwen
 * @date 2021/7/31 15:40
 * 消息分发器接口
 **/
public interface Dispatcher {

    /**
     * 消息分发
     * @param message 消息
     * @param session session
     */
    void dispatch(IMessage message, Session session);

}
