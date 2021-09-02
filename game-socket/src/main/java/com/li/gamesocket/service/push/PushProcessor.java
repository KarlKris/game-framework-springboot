package com.li.gamesocket.service.push;

import com.li.gamesocket.protocol.IMessage;

/**
 * @author li-yuanwen
 * 收到推送逻辑处理
 */
public interface PushProcessor {


    /**
     * 推送消息处理
     * @param message 推送消息
     */
    void process(IMessage message);

}
