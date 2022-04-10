package com.li.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * @author li-yuanwen
 */
@ComponentScan(basePackages = {"com.li"})
@EnableWebFlux
@SpringBootApplication
public class GameManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameManagerApplication.class, args);
    }

}
