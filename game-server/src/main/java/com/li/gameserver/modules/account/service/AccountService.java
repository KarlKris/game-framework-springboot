package com.li.gameserver.modules.account.service;

import com.li.gameremote.modules.account.vo.AccountVo;

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

    /**
     * 转换成账号信息展示VO
     * @param identity 身份标识
     * @return AccountVo
     */
    AccountVo transform(long identity);


    /**
     * 升级
     * @param identity 身份标识
     */
    void levelUp(long identity);

}
