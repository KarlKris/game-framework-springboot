package com.li.protocol.game.login.vo;

import com.li.network.anno.SocketResponse;
import com.li.protocol.game.login.protocol.GameServerLoginModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author li-yuanwen
 * @date 2022/5/11
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SocketResponse(module = GameServerLoginModule.MODULE, id = GameServerLoginModule.LOGIN)
public class ResGameLoginAccount {

    /** 玩家唯一标识 **/
    private long identity;

}
