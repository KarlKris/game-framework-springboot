package com.li.gameremote.modules.login.gateway.facade;

/**
 * @author li-yuanwen
 * 网关服登录模块
 */
public interface GatewayLoginModule {

    /** 模块号 **/
    short MODULE = 1;

    // 数据服 业务

    /** 创建账号 **/
    byte GAME_SERVER_CREATE = 1;

    /** 登录 **/
    byte GAME_SERVER_LOGIN = 2;

    /** 强退 **/
    byte KICK_OUT = -1;


}
