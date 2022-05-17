package com.li.protocol.gateway.login.protocol;

/**
 * @author li-yuanwen
 * 网关服登录模块
 */
public interface GatewayLoginModule {

    /** 模块号 **/
    short MODULE = 1002;

    // 数据服 业务

    /** 创建账号 **/
    byte CREATE_ACCOUNT = 1;

    /** 登录 **/
    byte LOGIN_ACCOUNT = 2;

    /** 强退 **/
    byte KICK_OUT = -1;


}
