package com.li.gameserver.modules.login.service;

/**
 * @author li-yuanwen
 */
public interface AccountService {


    /**
     * 创建账号
     * @param accountName 账号
     * @param channel 渠道标识
     * @return 账号标识
     */
    long createAccount(String accountName, int channel);


    /**
     * 登录账号
     * @param accountName 账号
     * @return 账号标识
     */
    long login(String accountName);

}
