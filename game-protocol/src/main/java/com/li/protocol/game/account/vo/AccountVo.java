package com.li.protocol.game.account.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:36
 * 账号VO
 **/
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountVo {

    /** 账号名 **/
    private String name;
    /** 玩家等级 **/
    private int level;

}
