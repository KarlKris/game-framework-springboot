﻿package com.li.gameserver.config;

import com.li.gamecommon.thread.GenericSerializedExecutorService;
import com.li.gamecommon.thread.SerializedExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author li-yuanwen
 * @date 2022/3/3
 */
@Configuration
public class GameConfig {

    @Bean
    public SerializedExecutorService executorService() {
        return new GenericSerializedExecutorService((Runtime.getRuntime().availableProcessors() >> 1) << 2, "游戏服业务分发线程");
    }

}
