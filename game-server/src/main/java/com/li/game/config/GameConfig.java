package com.li.game.config;

import cn.hutool.core.thread.NamedThreadFactory;
import com.li.common.concurrent.MultiThreadRunnableLoopGroup;
import com.li.common.concurrent.RunnableLoopGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author li-yuanwen
 * @date 2022/3/3
 */
@Configuration
public class GameConfig {

    @Bean
    public RunnableLoopGroup runnableLoopGroup() {
        return new MultiThreadRunnableLoopGroup(Runtime.getRuntime().availableProcessors() << 1
                , new NamedThreadFactory("游戏服业务线程-", false));
    }

}
