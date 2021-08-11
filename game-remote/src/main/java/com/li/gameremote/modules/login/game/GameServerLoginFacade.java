package com.li.gameremote.modules.login.game;

import com.li.gamesocket.anno.SocketModule;
import com.li.gamesocket.protocol.Response;
import com.li.gamesocket.service.session.Session;

/**
 * @author li-yuanwen
 * 游戏登录模块
 */
@SocketModule(module = GameServerLoginModule.MODULE)
public interface GameServerLoginFacade {


    /**
     * 创建账号
     * @param session session
     * @param account 账号
     * @param channel 渠道标识
     * @return /
     */
    Response<Long> create(Session session, String account, int channel);


    /**
     * 账号登录
     * @param session session
     * @param account 账号
     * @param channel 渠道标识
     * @return
     */
    Response<Long> login(Session session, String account, int channel);
}
