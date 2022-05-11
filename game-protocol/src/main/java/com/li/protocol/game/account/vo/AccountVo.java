package com.li.protocol.game.account.vo;

import com.li.network.anno.SocketResponse;
import com.li.protocol.game.account.protocol.ServerAccountModule;
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
@SocketResponse(module = ServerAccountModule.MODULE, id = ServerAccountModule.GET_SHOW_VO)
public class AccountVo {

    /** 账号名 **/
    private String name;
    /** 玩家等级 **/
    private int level;

}
