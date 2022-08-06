package com.li.protocol.game.account.protocol;

import com.li.network.anno.*;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:46
 * 账号模块接口
 **/
@SocketController(module = ServerAccountModule.MODULE)
public interface ServerAccountController {

    /**
     * 账号升级
     * @param identity 身份标识
     * @return
     */
    @SocketMethod(id = ServerAccountModule.LEVEL_UP)
    void levelUp(@Identity long identity);

}
