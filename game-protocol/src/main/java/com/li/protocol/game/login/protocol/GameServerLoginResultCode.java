package com.li.protocol.game.login.protocol;

/**
 * @author li-yuanwen
 */
public interface GameServerLoginResultCode {


    /** 重复创建 **/
    int CREATE_REPEAT = 1001;
    /** 账号未创建 **/
    int ACCOUNT_NOT_FOUND = 1002;
    /** 拒绝服务 **/
    int REJECT = 1003;

}
