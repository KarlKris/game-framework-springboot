package com.li.game.modules.account.service.impl;

import com.google.common.hash.*;
import com.li.common.exception.BadRequestException;
import com.li.core.cache.anno.*;
import com.li.core.cache.config.CachedType;
import com.li.engine.channelhandler.common.FirewallService;
import com.li.game.modules.account.manager.*;
import com.li.game.modules.account.service.AccountService;
import com.li.protocol.common.cache.CacheNameConstants;
import com.li.protocol.game.account.protocol.ServerAccountResultCode;
import com.li.protocol.game.account.vo.AccountVo;
import com.li.protocol.game.login.protocol.GameServerLoginResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.*;
import java.nio.charset.StandardCharsets;

/**
 * @author li-yuanwen
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {


    @Resource
    private FirewallService firewallService;
    @Resource
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
        Long id = accountManager.getIdByAccountName(accountName);
        if (id == null) {
            throw new BadRequestException(GameServerLoginResultCode.ACCOUNT_NOT_FOUND);
        }
        return accountManager.load(id).getId();
    }

    @Override
    @Cachedable(type = CachedType.REMOTE, name = CacheNameConstants.IDENTITY_TO_ACCOUNT_VO, key = "#identity")
    public AccountVo transform(long identity) {
        Account account = checkAccountAndThrow(identity);
        return new AccountVo(account.getAccountName(), account.getLevel());
    }

    @Override
    @CachedEvict(type = CachedType.REMOTE, name = CacheNameConstants.IDENTITY_TO_ACCOUNT_VO, key = "#identity")
    public void levelUp(long identity) {
        Account account = checkAccountAndThrow(identity);
        accountManager.levelUp(account);
    }

    @Override
    public void logout(long identity) {
        Account account = accountManager.load(identity);
        accountManager.logout(account);
    }

    private Account checkAccountAndThrow(long identity) {
        Account account = accountManager.load(identity);
        if (account == null) {
            throw new BadRequestException(ServerAccountResultCode.ACCOUNT_NOT_FOUND);
        }
        return account;
    }
}
