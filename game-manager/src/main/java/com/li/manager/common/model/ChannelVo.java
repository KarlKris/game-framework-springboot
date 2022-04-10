package com.li.manager.common.model;

import com.li.manager.common.entity.Channel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author li-yuanwen
 * 渠道信息
 */
@Data
@NoArgsConstructor
public class ChannelVo {


    /** 渠道标识 **/
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

    public ChannelVo(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.loginKey = channel.getLoginKey();
        this.chargeKey = channel.getChargeKey();
        this.whiteIps = channel.getWhiteIps();
        this.whiteAccounts = channel.getWhiteAccounts();
        this.createTime = channel.getCreateTime();
    }


}
