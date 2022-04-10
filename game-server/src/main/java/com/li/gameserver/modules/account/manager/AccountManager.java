package com.li.gameserver.modules.account.manager;

import com.li.common.id.MultiServerIdGenerator;
import com.li.core.cache.EntityCacheService;
import com.li.core.cache.anno.Cachedable;
import com.li.core.dao.core.DataFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author li-yuanwen
 */
@Component
@Slf4j
public class AccountManager {

    @Resource
    private EntityCacheService cacheService;
    @Resource
    private DataFinder dataFinder;
    @Resource
    private MultiServerIdGenerator idGenerator;

    public Collection<String> getAllRegisterAccounts() {
        return dataFinder.query(Account.class, String.class, Account.ALL_ACCOUNT_NAME);
    }

    public Account create(String accountName, int channel) {
        return cacheService.createEntity(Account.of(idGenerator.nextId(), channel, accountName));
    }

    public Account load(long id) {
        return cacheService.loadEntity(id, Account.class);
    }

    public void levelUp(Account account) {
        account.levelUp();
    }

    public void login(Account account) {
        account.login();
    }

    public void logout(Account account) {
        account.logout();
    }


    @Cachedable(name = "AccountName2Id", key = "#accountName")
    public Long getIdByAccountName(String accountName) {
        return dataFinder.uniqueQuery(Account.class, Long.TYPE, Account.GET_ID_BY_ACCOUNT_NAME, accountName);
    }

}
