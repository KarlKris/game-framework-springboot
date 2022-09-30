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
        FightUnit unit2 = newBattleUnit(scene, CampType.ATTACKER, new Vector2D(1000, 0));
        FightUnit unit3 = newBattleUnit(scene, CampType.ATTACKER, new Vector2D(2000, 0));
        FightUnit unit4 = newBattleUnit(scene, CampType.ATTACKER, new Vector2D(3000, 0));
        FightUnit unit5 = newBattleUnit(scene, CampType.ATTACKER, new Vector2D(4000, 0));
        FightUnit unit6 = newBattleUnit(scene, CampType.ATTACKER, new Vector2D(5000, 0));
        FightUnit unit7 = newBattleUnit(scene, CampType.ATTACKER, new Vector2D(6000, 0));
        FightUnit unit8 = newBattleUnit(scene, CampType.ATTACKER, new Vector2D(7000, 0));
        FightUnit unit9 = newBattleUnit(scene, CampType.ATTACKER, new Vector2D(8000, 0));

        FightUnit unit10 = newBattleUnit(scene, CampType.DEFENDER, new Vector2D(10000, 10000));
        FightUnit unit11 = newBattleUnit(scene, CampType.DEFENDER, new Vector2D(9000, 10000));
        FightUnit unit12 = newBattleUnit(scene, CampType.DEFENDER, new Vector2D(8000, 10000));
        FightUnit unit13 = newBattleUnit(scene, CampType.DEFENDER, new Vector2D(7000, 10000));
        FightUnit unit14 = newBattleUnit(scene, CampType.DEFENDER, new Vector2D(6000, 10000));
        FightUnit unit15 = newBattleUnit(scene, CampType.DEFENDER, new Vector2D(5000, 10000));
        FightUnit unit16 = newBattleUnit(scene, CampType.DEFENDER, new Vector2D(4000, 10000));
        FightUnit unit17 = newBattleUnit(scene, CampType.DEFENDER, new Vector2D(3000, 10000));
        FightUnit unit18 = newBattleUnit(scene, CampType.DEFENDER, new Vector2D(2000, 10000));


        scene.enterScene(unit1);
        scene.enterScene(unit2);
        scene.enterScene(unit3);
        scene.enterScene(unit4);
        scene.enterScene(unit5);

        scene.enterScene(unit6);
        scene.enterScene(unit7);
        scene.enterScene(unit8);
        scene.enterScene(unit9);
        scene.enterScene(unit10);

        scene.enterScene(unit11);
        scene.enterScene(unit12);
        scene.enterScene(unit13);
        scene.enterScene(unit14);
        scene.enterScene(unit15);
        scene.enterScene(unit16);
        scene.enterScene(unit17);
        scene.enterScene(unit18);
    }

    public static FightUnit newBattleUnit(BattleScene scene, CampType type, Vector2D point) {
        Map<Attribute, Long> baseAttributes = new HashMap<>(3);
        long hp = RandomUtil.randomLong(2000, 4500);
        baseAttributes.put(Attribute.HP_MAX, hp);
        baseAttributes.put(Attribute.CUR_HP, hp);
        long pa = RandomUtil.randomLong(125, 280);
        baseAttributes.put(Attribute.PHYSICAL_ATTACK, pa);

        long curRound = scene.getSceneRound();
        List<Skill> skills = new ArrayList<>(2);
        skills.add(new Skill(1001, curRound));
        skills.add(new Skill(1004, curRound));

        int speed = RandomUtil.randomInt(250, 320);

        return new BattleHero(scene.getNextId(), UnitType.HERO, type, 1000, speed, point, baseAttributes, skills);
    }

}
