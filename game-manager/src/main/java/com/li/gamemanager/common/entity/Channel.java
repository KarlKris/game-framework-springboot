package com.li.gamemanager.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author li-yuanwen
 * 渠道
 */
@Document
@Getter
@NoArgsConstructor
public class Channel {

    /** 渠道标识 **/
    @Id
    private int id;

    /** 渠道名称 **/
    private String name;

    /** 登录key **/
    private String loginKey;

    /** 充值key **/
    private String chargeKey;

    /** 白名单ip **/
    private List<String> whiteIps;

    /** 白名单账号 **/
    private List<String> whiteAccounts;

    /** 创建时间 **/
    private long createTime;

    public Channel(int id, String name, String loginKey, String chargeKey, List<String> whiteIps, List<String> whiteAccounts) {
        this.id = id;
        this.name = name;
        this.loginKey = loginKey;
        this.chargeKey = chargeKey;
        this.whiteIps = whiteIps;
        this.whiteAccounts = whiteAccounts;
        this.createTime = System.currentTimeMillis();
    }

}
