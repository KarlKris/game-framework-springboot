package com.li.battle.skill;

import com.li.battle.core.scene.BattleScene;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 技能容器
 * @author li-yuanwen
 * @date 2022/5/26
 */
public class SkillManager {


    /** 关联的战斗场景 **/
    private final BattleScene scene;

    /** 待处理的buff队列 **/
    private final PriorityQueue<BattleSkill> queue = new PriorityQueue<>(Comparator.comparingLong(BattleSkill::getNextRound));


    public SkillManager(BattleScene scene) {
        this.scene = scene;
    }

    public void addBattleSkill(BattleSkill skill) {
        handle(skill, scene.getSceneRound());
    }

    public void removeBattleSkill(long ownerId) {
        queue.removeIf(skill -> skill.getOwner() == ownerId);
    }


    public void update() {
        BattleSkill element = queue.peek();
        long curRound = scene.getSceneRound();
        while (element != null && element.getNextRound() <= curRound) {
            queue.poll();
            handle(element, curRound);
            element = queue.peek();
        }
    }

    private void handle(BattleSkill skill, long curRound) {
        if (skill.isExpire(curRound)) {
            return;
        }
        scene.battleSceneHelper().battleSkillExecutor().execute(skill);
        if (!skill.isExpire(curRound)) {
            queue.offer(skill);
        }
    }


}
