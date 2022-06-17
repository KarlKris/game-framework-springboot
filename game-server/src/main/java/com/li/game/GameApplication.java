package com.li.game;

import com.li.common.resource.anno.EnableResourceScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.li"})
@EnableResourceScan(value = {"com/li/game"}, path = "${resource.root.path}")
@EnableTransactionManagement
public class GameApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameApplication.class, args);
    }

}
