package com.li.gateway.commom;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * 系统配置
 */
@Configuration
@ConfigurationProperties(prefix = "server.system")
@PropertySource("classpath:server.properties")
@Component
@Data
public class GatewaySystemConfig {

    /** 签名有效时长(秒) <0 不限制 **/
    private int validTime;
    /** 登录秘钥 **/
    private String loginKey;
}
