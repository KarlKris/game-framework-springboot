package com.li.gameserver.modules.account.manager;

import cn.hutool.core.date.DateUtil;
import com.li.gamecore.dao.IEntity;
import com.li.gamecore.dao.anno.Enhance;
import lombok.Getter;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author li-yuanwen
 */
@Entity
@Proxy(lazy = false)
@NamedQueries({
        @NamedQuery(name = Account.ALL_ACCOUNT_NAME, query = "SELECT a.accountName FROM Account AS a"),
        @NamedQuery(name = Account.GET_ID_BY_ACCOUNT_NAME, query = "SELECT a.id FROM Account AS a WHERE a.accountName = ?0")
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

    /** 账号等级 **/
    private int level = 1;

    /** 最近登录时间 **/
    private long loginTime;

    /** 最近登出时间 **/
    private long logoutTime;

    /** 今日在线总时长(毫秒) **/
    private long dayOnlineTime;

    /** 账号在线总时长(毫秒) **/
    private long totalOnlineTime;

    @Override
    public Long getId() {
        return id;
    }

    @Enhance
    void levelUp() {
        this.level++;
    }

    @Enhance
    void login() {
        this.loginTime = System.currentTimeMillis();
    }

    @Enhance
    void logout() {
        this.logoutTime = System.currentTimeMillis();

        if (DateUtil.isSameDay(new Date(this.loginTime), new Date(this.logoutTime))) {
            this.dayOnlineTime += (this.logoutTime - this.loginTime);
            this.totalOnlineTime += (this.logoutTime - this.loginTime);
        } else {
            // todo 跨天登出
        }
    }

    public static Account of(long id, int channel, String accountName) {
        Account account = new Account();
        account.id = id;
        account.channel = channel;
        account.accountName = accountName;
        return account;
    }
}
