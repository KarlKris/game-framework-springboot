package com.li.gameremote.modules.chat.facade;

/**
 * @author li-yuanwen
 * 聊天模块
 */
public interface ChatModule {

    /** 聊天模块号 **/
    short MODULE = 3;

    /** 发送消息 **/
    byte SEND = 1;

    // ----------- 推送 ---------------------

    /** 消息推送 **/
    byte PUSH = -1;

}
