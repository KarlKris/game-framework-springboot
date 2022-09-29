package com.li.battle;

import cn.hutool.core.util.RandomUtil;
import com.li.battle.core.*;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;
import com.li.battle.service.BattleService;
import com.li.common.resource.anno.EnableResourceScan;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * @author li-yuanwen
 * @date 2022/6/7
 */
@SpringBootApplication(scanBasePackages = {"com.li"})
@EnableResourceScan(value = {"com/li/battle"}, path = "${resource.root.path}")
public class BattleApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BattleApplication.class, args);
        BattleService bean = context.getBean(BattleService.class);
        BattleScene scene = bean.createScene(21003);

        FightUnit unit1 = newBattleUnit(scene, CampType.ATTACKER, new Vector2D(0, 0));
        FightUnit unit2 = newBattleUnit(scene, CampType.DEFENDER, new Vector2D(10000, 10000));
        scene.enterScene(unit1);
        scene.enterScene(unit2);
    }

    public static FightUnit newBattleUnit(BattleScene scene, CampType type, Vector2D point) {
        Map<Attribute, Long> baseAttributes = new HashMap<>(3);
        long hp = RandomUtil.randomLong(1500, 2000);
        baseAttributes.put(Attribute.HP_MAX, hp);
        baseAttributes.put(Attribute.CUR_HP, hp);
        long pa = RandomUtil.randomLong(50, 100);
        baseAttributes.put(Attribute.PHYSICAL_ATTACK, pa);

        long curRound = scene.getSceneRound();
        List<Skill> skills = new ArrayList<>(2);
        skills.add(new Skill(1001, curRound));
        skills.add(new Skill(1004, curRound));

        return new BattleHero(scene.getNextId(), UnitType.HERO, type, 1000, 3000, point, baseAttributes, skills);
    }

}
