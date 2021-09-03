package com.li.gameremote.modules.account.facade;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:44
 * 账号模块
 **/
public interface ServerAccountModule {

    /** 模块号 **/
    short MODULE = 5;


    /** 获取账号展示VO **/
    byte GET_SHOW_VO = 1;

    /** 升级 **/
    byte LEVEL_UP = 2;
}
