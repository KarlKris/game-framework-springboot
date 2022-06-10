package com.li.battle;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.service.BattleService;
import com.li.common.resource.anno.ResourceScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author li-yuanwen
 * @date 2022/6/7
 */
@SpringBootApplication(scanBasePackages = {"com.li"})
@ResourceScan(value = {"com/li/battle"}, path = "${resource.root.path}")
public class BattleApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BattleApplication.class, args);
        BattleService bean = context.getBean(BattleService.class);
        BattleScene scene = bean.createScene(21002);
    }

}
