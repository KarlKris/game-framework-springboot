package com.li.protocol.game.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 游戏服登陆账号
 * @author li-yuanwen
 * @date 2021/12/16
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqGameLoginAccount {

    /** 账号 **/
    private String account;
    /** 渠道 **/
    private int channel;

}
