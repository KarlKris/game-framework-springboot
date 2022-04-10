package com.li.manager.common.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ras 配置
 * @author li-yuanwen
 * @date 2021/8/28 23:30
 **/
@Getter
@Component
public class RsaProperties {

    private String privateKey;

    @Value("${rsa.private_key}")
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
