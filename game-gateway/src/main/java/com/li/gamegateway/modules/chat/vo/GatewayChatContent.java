package com.li.gamegateway.modules.chat.vo;

import com.li.protocol.game.account.vo.AccountVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author li-yuanwen
 * @date 2021/9/2 22:04
 **/
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GatewayChatContent {

    /** 发送人账号信息 **/
    private AccountVo accountVo;

    /** 信息 **/
    private String msg;

}
