package com.li.gameremote.modules.chat.vo;

import lombok.Getter;

/**
 * @author li-yuanwen
 * 聊天消息
 */
@Getter
public class ChatContent {

    /** 发送玩家标识 **/
    private long senderId;
    /** 消息内容 **/
    private String msg;

    public static ChatContent of(long senderId, String msg) {
        ChatContent content = new ChatContent();
        content.senderId = senderId;
        content.msg = msg;
        return content;
    }

}
