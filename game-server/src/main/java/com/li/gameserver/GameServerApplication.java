package com.li.gameserver;

import com.li.gamecommon.resource.anno.ResourceScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.li"})
@ResourceScan(value = {"com/li/gameserver"}, path = "${resource.root.path}")
@EnableTransactionManagement
public class GameServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameServerApplication.class, args);
    }

}
