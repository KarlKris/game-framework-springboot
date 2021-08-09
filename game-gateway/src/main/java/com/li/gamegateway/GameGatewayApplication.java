package com.li.gamegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.li.gamesocket","com.li.gamecore","com.li.gamecluster"})
public class GameGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameGatewayApplication.class, args);
    }

}
