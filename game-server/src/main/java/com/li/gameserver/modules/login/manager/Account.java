package com.li.gameserver.modules.login.manager;

import com.li.gamecore.dao.IEntity;
import lombok.Getter;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author li-yuanwen
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Account.ALL_ACCOUNT_NAME, query = "SELECT a.accountName FROM Account AS a"),
        @NamedQuery(name = Account.GET_ID_BY_ACCOUNT_NAME, query = "SELECT a.id FROM Account AS a WHERE a.accountName = ?1")
})

@Getter
public class Account implements IEntity<Long> {

    public static final String ALL_ACCOUNT_NAME = "ALL_ACCOUNT_NAME";
    public static final String GET_ID_BY_ACCOUNT_NAME = "GET_ID_BY_ACCOUNT_NAME";

    /** 玩家id **/
    @Id
    private long id;

    /** 账号名 **/
    private String accountName;

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
        account.accountName = accountName;
        return account;
    }
}
