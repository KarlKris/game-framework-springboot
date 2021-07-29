package com.li.gamesocket.ssl;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author li-yuanwen
 */
@Configuration
@Getter
public class SslConfig {

    /** SSL开关 **/
    @Value("${netty.ssl.enable:false}")
    private boolean sllEnable;

    /** SSL单向认证开关 true单向 false双向 **/
    @Value("${netty.ssl.oneWay:false}")
    private boolean oneWay;

    /** ssl 协议 **/
    @Value("${netty.ssl.protocol:}")
    private String protocol;

    /** ssl 证书类型 **/
    @Value("${netty.ssl.store.type:}")
    private String storeType;

    /** ssl 加密算法 **/
    @Value("${netty.ssl.algorithm:}")
    private String algorithm;

    /** ssl 密码 **/
    @Value("${netty.ssl.password:}")
    private String password;

    /** ssl pkPath **/
    @Value("${netty.ssl.pkPath:}")
    private String pkPath;

    /** ssl caPath **/
    @Value("${netty.ssl.caPath:}")
    private String caPath;

    public SslMode getSslMode() {
        return oneWay ? SslMode.CA : SslMode.CAS;
    }

}
