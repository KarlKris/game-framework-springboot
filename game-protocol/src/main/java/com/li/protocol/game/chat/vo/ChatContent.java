package com.li.protocol.game.chat.vo;

import com.li.protocol.game.account.vo.AccountVo;
import lombok.Getter;

/**
 * @author li-yuanwen
 * 聊天消息
 */
@Getter
public class ChatContent {

    /** 发送人账号信息 **/
    private AccountVo accountVo;
    /** 消息内容 **/
    private String msg;

    public static ChatContent of(AccountVo accountVo, String msg) {
        ChatContent content = new ChatContent();
        content.accountVo = accountVo;
        content.msg = msg;
        return content;
    }

}
