package com.li.gamegateway.config;

import com.li.gamecommon.thread.GenericSerializedExecutorService;
import com.li.gamecommon.thread.SerializedExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author li-yuanwen
 * @date 2022/3/3
 */
@Configuration
public class GatewayConfig {

    @Bean
    public SerializedExecutorService executorService() {
        return new GenericSerializedExecutorService((Runtime.getRuntime().availableProcessors() >> 1) << 2, "网关服业务分发线程");
    }

}
