package com.li.gameserver.modules.login.service.impl;

import com.li.gamecommon.common.MultiServerIdGenerator;
import com.li.gameserver.modules.login.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author li-yuanwen
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {


    @Autowired
    private MultiServerIdGenerator idGenerator;

    // todo 账号检测使用布隆过滤器

    @Override
    public long createAccount(String account, int channel) {
        return 0;
    }

    @Override
    public long login(String account, int channel) {
        return 0;
    }
}
