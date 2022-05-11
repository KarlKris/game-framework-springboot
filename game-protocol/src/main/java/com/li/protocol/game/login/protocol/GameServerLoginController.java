package com.li.protocol.game.login.protocol;


import com.li.network.anno.*;
import com.li.network.session.ServerSession;
import com.li.protocol.game.login.dto.ReqGameCreateAccount;
import com.li.protocol.game.login.dto.ReqGameLoginAccount;

/**
 * @author li-yuanwen
 * 游戏登录模块
 */
@SocketController(module = GameServerLoginModule.MODULE)
public interface GameServerLoginController {


    /**
     * 创建账号
     * @param session session
     * @param reqGameCreateAccount 相关信息
     * @return 玩家标识
     */
    @SocketMethod(id = GameServerLoginModule.CREATE)
    Long create(@Session ServerSession session
            , @InBody ReqGameCreateAccount reqGameCreateAccount);


    /**
     * 账号登录
     * @param session session
     * @param reqGameLoginAccount 相关信息
     * @return 玩家标识
     */
    @SocketMethod(id = GameServerLoginModule.LOGIN)
    Long login(@Session ServerSession session
            , @InBody ReqGameLoginAccount reqGameLoginAccount);


    /**
     * 账号登出
     * @param session session
     * @param identity 玩家标识
     */
    @SocketMethod(id = GameServerLoginModule.LOGOUT)
    void logout(@Session ServerSession session, @Identity long identity);

}
