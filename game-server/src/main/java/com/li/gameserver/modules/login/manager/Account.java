package com.li.gameserver.modules.login.manager;

import com.li.gamecore.dao.IEntity;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author li-yuanwen
 */
@Entity
@Getter
public class Account implements IEntity<Long> {

    /** 玩家id **/
    @Id
    private long id;

    /** 账号名 **/
    private String account;

    /** 渠道标识 **/
    private int channel;

    @Override
    public Long getId() {
        return id;
    }

    public static Account of(long id, int channel, String accountName) {
        Account account = new Account();
        account.id = id;
        account.channel = channel;
        account.account = accountName;
        return account;
    }
}
