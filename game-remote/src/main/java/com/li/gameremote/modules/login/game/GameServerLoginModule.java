package com.li.gameremote.modules.login.game;

/**
 * @author li-yuanwen
 * 游戏服登录模块
 */
public interface GameServerLoginModule {

    /** 模块号 **/
    short MODULE = 2;

    /** 创建账号 **/
    byte CREATE = 1;

    /** 登录 **/
    byte LOGIN = 2;
}
