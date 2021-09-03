package com.li.gameremote.modules.account.facade;

import com.li.gameremote.modules.account.vo.AccountVo;
import com.li.gamesocket.anno.Identity;
import com.li.gamesocket.anno.InBody;
import com.li.gamesocket.anno.SocketCommand;
import com.li.gamesocket.anno.SocketModule;
import com.li.gamesocket.protocol.Response;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:46
 * 账号模块接口
 **/
@SocketModule(module = ServerAccountModule.MODULE)
public interface ServerAccountFacade {


    /**
     * 获取账号信息展示VO
     * @param identity 身份标识
     * @return AccountVo
     */
    @SocketCommand(command = ServerAccountModule.GET_SHOW_VO)
    Response<AccountVo> getShowVo(@InBody(name = "identity") long identity);

    /**
     * 账号升级
     * @param identity 身份标识
     * @return
     */
    @SocketCommand(command = ServerAccountModule.LEVEL_UP)
    void levelUp(@Identity long identity);

}
