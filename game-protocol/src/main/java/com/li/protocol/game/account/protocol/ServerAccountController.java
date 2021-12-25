package com.li.protocol.game.account.protocol;

import com.li.protocol.game.account.vo.AccountVo;
import com.li.network.anno.Identity;
import com.li.network.anno.InBody;
import com.li.network.anno.SocketController;
import com.li.network.anno.SocketMethod;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:46
 * 账号模块接口
 **/
@SocketController(module = ServerAccountModule.MODULE)
public interface ServerAccountController {


    /**
     * 获取账号信息展示VO
     * @param identity 身份标识
     * @return AccountVo
     */
    @SocketMethod(id = ServerAccountModule.GET_SHOW_VO)
    AccountVo getShowVo(@InBody long identity);

    /**
     * 账号升级
     * @param identity 身份标识
     * @return
     */
    @SocketMethod(id = ServerAccountModule.LEVEL_UP)
    void levelUp(@Identity long identity);

}
