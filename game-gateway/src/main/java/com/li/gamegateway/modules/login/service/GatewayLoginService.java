package com.li.gamegateway.modules.login.service;

import com.li.gamesocket.service.session.Session;

/**
 * @author li-yuanwen
 * 网关服登录Service
 */
public interface GatewayLoginService {


    /**
     * 创建账号
     * @param session session
     * @param account 账号
     * @param channel 渠道标识
     * @param serverId 服务器标识
     */
    void create(Session session, String account, int channel, int serverId);


    /**
     * 账号登录
     * @param session session
     * @param account 账号
     * @param channel 渠道标识
     * @param serverId 服务器标识
     */
    void login(Session session, String account, int channel, int serverId);

}
