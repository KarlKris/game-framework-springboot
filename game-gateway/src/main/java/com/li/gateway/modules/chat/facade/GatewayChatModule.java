package com.li.gateway.modules.chat.facade;

/**
 * @author li-yuanwen
 * @date 2021/9/2 22:01
 * 网关服聊天模块
 **/
public interface GatewayChatModule {

    /** 模块号 **/
    short MODULE = 4;

    /** 聊天推送 **/
    byte PUSH_MESSAGE = -1;

}
