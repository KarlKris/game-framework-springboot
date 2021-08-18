package com.li.gameserver.modules.login.manager;

import com.li.gamecore.dao.IEntity;
import lombok.Getter;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author li-yuanwen
 */
@Table
@Getter
public class Account implements IEntity<Long> {

    /** 玩家id **/
    @Id
    private long id;

    /** 账号名 **/
    private String account;

    @Override
    public Long getId() {
        return id;
    }

    public static Account of(long id, String accountName) {
        Account account = new Account();
        account.id = id;
        account.account = accountName;
        return account;
    }
}
