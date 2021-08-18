package com.li.gamegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ComponentScan(basePackages = {
        "com.li.gamesocket",
        "com.li.gamecommon",
        "com.li.gamecluster",
        "com.li.gamegateway",
        "com.li.gamecore"
})
public class GameGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameGatewayApplication.class, args);
    }

}
