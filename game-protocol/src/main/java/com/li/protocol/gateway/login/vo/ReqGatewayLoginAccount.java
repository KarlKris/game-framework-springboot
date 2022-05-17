package com.li.protocol.gateway.login.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 网关层登陆账号参数
 * @author li-yuanwen
 * @date 2021/12/16
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqGatewayLoginAccount {

    /** 账号 **/
    private String account;
    /** 渠道 **/
    private int channel;
    /** 服标识 **/
    private int serverId;
    /** 时间戳(精确到秒) **/
    private int timestamp;
    /** 签名 **/
    private String sign;

}