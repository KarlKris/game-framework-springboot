package com.li.protocol.game.login.protocol;


import com.li.network.anno.*;
import com.li.network.session.ServerSession;
import com.li.protocol.game.login.vo.ReqGameCreateAccount;
import com.li.protocol.game.login.vo.ReqGameLoginAccount;
import com.li.protocol.game.login.vo.ResGameCreateAccount;
import com.li.protocol.game.login.vo.ResGameLoginAccount;

import java.util.concurrent.CompletableFuture;

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
    CompletableFuture<ResGameCreateAccount> create(@Session ServerSession session
            , @InBody ReqGameCreateAccount reqGameCreateAccount);


    /**
     * 账号登录
     * @param session session
     * @param reqGameLoginAccount 相关信息
     * @return 玩家标识
     */
    @SocketMethod(id = GameServerLoginModule.LOGIN)
    CompletableFuture<ResGameLoginAccount> login(@Session ServerSession session
            , @InBody ReqGameLoginAccount reqGameLoginAccount);


    /**
     * 账号登出
     * @param session session
     * @param identity 玩家标识
     */
    @SocketMethod(id = GameServerLoginModule.LOGOUT)
    void logout(@Session ServerSession session, @Identity long identity);

}
