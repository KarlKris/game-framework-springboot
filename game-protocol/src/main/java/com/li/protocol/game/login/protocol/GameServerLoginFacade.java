package com.li.protocol.game.login.protocol;


import com.li.network.anno.InBody;
import com.li.network.anno.SocketController;
import com.li.network.anno.SocketMethod;
import com.li.network.session.ServerSession;
import com.li.protocol.game.login.dto.ReqGameCreateAccount;
import com.li.protocol.game.login.dto.ReqGameLoginAccount;

/**
 * @author li-yuanwen
 * 游戏登录模块
 */
@SocketController(module = GameServerLoginModule.MODULE)
public interface GameServerLoginFacade {


    /**
     * 创建账号
     * @param session session
     * @param reqGameCreateAccount 相关信息
     * @return 玩家标识
     */
    @SocketMethod(id = GameServerLoginModule.CREATE)
    Long create(ServerSession session
            , @InBody ReqGameCreateAccount reqGameCreateAccount);


    /**
     * 账号登录
     * @param session session
     * @param reqGameLoginAccount 相关信息
     * @return 玩家标识
     */
    @SocketMethod(id = GameServerLoginModule.LOGIN)
    Long login(ServerSession session
            , @InBody ReqGameLoginAccount reqGameLoginAccount);
}
