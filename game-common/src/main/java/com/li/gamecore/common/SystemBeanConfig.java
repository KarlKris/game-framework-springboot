package com.li.gamecore.common;

import com.li.gamecore.rpc.LocalServerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.SocketException;

/**
 * @author li-yuanwen
 */
@Configuration
public class SystemBeanConfig {

    /** id生成器 **/
    @Bean
    @ConditionalOnBean(LocalServerService.class)
    public MultiServerIdGenerator multiServerIdGenerator(LocalServerService serverService) throws SocketException {
        return new MultiServerIdGenerator(Short.parseShort(serverService.getLocalServerInfo().getId()));
    }
}
