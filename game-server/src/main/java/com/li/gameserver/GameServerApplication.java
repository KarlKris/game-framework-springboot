package com.li.gameserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.li.gamesocket",
        "com.li.gamecommon",
        "com.li.gamecluster",
        "com.li.gameserver",
        "com.li.gamecore"
})
@EntityScan(basePackages = "com.li.gameserver.modules")
public class GameServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameServerApplication.class, args);
    }

}
