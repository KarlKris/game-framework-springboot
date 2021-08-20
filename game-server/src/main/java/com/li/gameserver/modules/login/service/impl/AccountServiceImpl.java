package com.li.gameserver.modules.login.service.impl;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.li.gamecommon.exception.BadRequestException;
import com.li.gameremote.modules.login.game.GameServerLoginResultCode;
import com.li.gameserver.modules.login.manager.Account;
import com.li.gameserver.modules.login.manager.AccountManager;
import com.li.gameserver.modules.login.service.AccountService;
import com.li.gamesocket.channelhandler.FirewallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * @author li-yuanwen
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {


    @Autowired
    private FirewallService firewallService;
    @Autowired
    private AccountManager accountManager;

    /** 账号检测使用布隆过滤器 **/
    private BloomFilter<String> accountNames = null;

    @PostConstruct
    private void init() {
        // 预计插入数 最大连接数*8
        int expectInsertions = firewallService.getMaxConnectNum() << 3;
        this.accountNames = BloomFilter.<String>create(Funnels.stringFunnel(StandardCharsets.UTF_8), expectInsertions, 0.00001);

        accountManager.getAllRegisterAccounts().forEach(k -> accountNames.put(k));

    }

    @Override
    public long createAccount(String accountName, int channel) {
        if (accountNames.mightContain(accountName)) {
            throw new BadRequestException(GameServerLoginResultCode.CREATE_REPEAT);
        }
        // 插入数据中
        accountNames.put(accountName);
        return accountManager.create(accountName, channel).getId();
    }

    @Override
    public long login(String accountName) {
        if (!accountNames.mightContain(accountName)) {
            throw new BadRequestException(GameServerLoginResultCode.ACCOUNT_NOT_FOUND);
        }
        Account account = accountManager.load(accountName);
        if (account == null) {
            throw new BadRequestException(GameServerLoginResultCode.ACCOUNT_NOT_FOUND);
        }
        return account.getId();
    }
}