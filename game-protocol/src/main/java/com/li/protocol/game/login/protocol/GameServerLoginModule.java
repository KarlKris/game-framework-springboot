package com.li.protocol.game.login.protocol;

/**
 * @author li-yuanwen
 * 游戏服登录模块
 */
public interface GameServerLoginModule {

    /** 模块号 **/
    short MODULE = 3;

    /** 创建账号 **/
    byte CREATE = 1;

    /** 登录 **/
    byte LOGIN = 2;

    /** 登出 **/
    byte LOGOUT = 3;
}
