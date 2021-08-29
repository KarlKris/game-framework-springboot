package com.li.gamemanager.common.config;

import com.li.gamemanager.common.properties.LoginProperties;
import com.li.gamemanager.common.properties.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @apiNote 配置文件转换Pojo类的 统一配置 类
 * @author li-yuanwen
 * @date 2021/8/28 13:56
 **/
@Configuration
public class PropertiesBeanConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "login")
    public LoginProperties loginProperties() {
        return new LoginProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "jwt")
    public SecurityProperties securityProperties() {
        return new SecurityProperties();
    }
}
