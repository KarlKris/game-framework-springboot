package com.li.game.config;

import com.li.common.concurrent.IdentityThreadFactoryExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author li-yuanwen
 * @date 2022/3/3
 */
@Configuration
public class GameConfig {

    @Bean
    public IdentityThreadFactoryExecutor runnableLoopGroup() {
        return new IdentityThreadFactoryExecutor();
    }

}
