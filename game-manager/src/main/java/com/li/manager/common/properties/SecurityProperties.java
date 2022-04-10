package com.li.manager.common.properties;

import lombok.Data;

/**
 * Jwt参数配置
 * @author li-yuanwen
 * @date 2021/8/28 14:29
 **/
@Data
public class SecurityProperties {

    /** Request Headers ： Authorization **/
    private String header;

    /** 令牌前缀，最后留个空格 Bearer **/
    private String tokenStartWith;

    /** 必须使用最少88位的Base64对该令牌进行编码 **/
    private String base64Secret;

    /** 令牌过期时间 此处单位/秒 **/
    private int tokenValidityInSeconds;

    /**  token 续期检查 **/
    private long detect;

    /** 续期时间 **/
    private long renew;

    public String getTokenStartWith() {
        return tokenStartWith + " ";
    }
}
