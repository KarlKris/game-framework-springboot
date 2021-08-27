package com.li.gameserver.modules.chat.service;

/**
 * @author li-yuanwen
 */
public interface ChatService {


    /**
     * 发送消息
     * @param identity 发送玩家标识
     * @param msg 消息
     */
    void send(long identity, String msg);

}
