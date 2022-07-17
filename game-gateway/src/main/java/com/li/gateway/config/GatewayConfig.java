package com.li.gateway.config;

import cn.hutool.core.thread.NamedThreadFactory;
import com.li.common.concurrency.MultiThreadRunnableLoopGroup;
import com.li.common.concurrency.RunnableLoopGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author li-yuanwen
 * @date 2022/3/3
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RunnableLoopGroup runnableLoopGroup() {
        return new MultiThreadRunnableLoopGroup(Runtime.getRuntime().availableProcessors() << 1
                , new NamedThreadFactory("网关服业务线程-", false));
    }

}
