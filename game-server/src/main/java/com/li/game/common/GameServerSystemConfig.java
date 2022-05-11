package com.li.game.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author li-yuanwen
 * 游戏配置
 */
@Configuration
@PropertySource("classpath:server.properties")
@Getter
public class GameServerSystemConfig {


    /** 游戏混渠道 **/
    @Value("#{'${server.system.channels}'.split(',')}")
    private int[] channels;

}
