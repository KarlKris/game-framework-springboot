package com.li.gamemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * @author li-yuanwen
 */
@SpringBootApplication
@EnableWebFlux
public class GameManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameManagerApplication.class, args);
    }

}
