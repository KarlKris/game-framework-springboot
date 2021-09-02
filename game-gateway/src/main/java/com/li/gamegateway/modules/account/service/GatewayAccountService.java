package com.li.gamegateway.modules.account.service;

import com.li.gameremote.modules.account.vo.AccountVo;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:35
 * 网关服账号Service
 **/
public interface GatewayAccountService {

    /**
     * 根据身份标识转换成展示VO
     * @param identity 身份标识
     * @return accountVo
     */
    AccountVo transformById(long identity);

}
