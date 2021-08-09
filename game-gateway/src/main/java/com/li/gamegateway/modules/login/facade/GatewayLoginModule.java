package com.li.gamegateway.modules.login.facade;

/**
 * @author li-yuanwen
 * 网关服登录模块
 */
public interface GatewayLoginModule {

    /** 模块号 **/
    short MODULE = 1;

    // 业务

    /** 创建账号 **/
    byte GAME_SERVER_CREATE = 1;

    /** 登录 **/
    byte GAME_SERVER_LOGIN = 2;

}
