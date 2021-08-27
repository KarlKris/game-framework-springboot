package com.li.gameserver.modules.login.manager;

import com.li.gamecommon.common.MultiServerIdGenerator;
import com.li.gamecore.cache.anno.Cachedable;
import com.li.gamecore.dao.core.DataBaseQuerier;
import com.li.gamecore.dao.service.EntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author li-yuanwen
 */
@Component
@Slf4j
public class AccountManager {

    @Autowired
    private EntityService entityService;
    @Autowired
    private DataBaseQuerier dataBaseQuerier;
    @Autowired
    private MultiServerIdGenerator idGenerator;

    public Collection<String> getAllRegisterAccounts() {
        return dataBaseQuerier.query(Account.class, String.class, Account.ALL_ACCOUNT_NAME);
    }

    public Account create(String accountName, int channel) {
        return entityService.create(Account.of(idGenerator.nextId(), channel, accountName));
    }

    public Account load(long id) {
        return entityService.load(id, Account.class);
    }


    @Cachedable(name = "AccountName2Id", key = "#accountName")
    public Long getIdByAccountName(String accountName) {
        return dataBaseQuerier.uniqueQuery(Account.class, Long.TYPE, Account.GET_ID_BY_ACCOUNT_NAME, accountName);
    }

}
