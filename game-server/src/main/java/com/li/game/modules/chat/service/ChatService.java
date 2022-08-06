package com.li.game.modules.chat.service;

import com.li.protocol.game.chat.vo.ChatContent;

/**
 * @author li-yuanwen
 */
public interface ChatService {


    /**
     * 发送消息
     * @param identity 发送玩家标识
     * @param msg 消息
     */
    void send(long identity, ChatContent msg);

}
