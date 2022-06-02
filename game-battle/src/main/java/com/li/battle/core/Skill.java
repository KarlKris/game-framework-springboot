package com.li.battle.core;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.resource.SkillConfig;
import lombok.Getter;

/**
 * 技能信息
 * @author li-yuanwen
 * @date 2022/5/30
 */
@Getter
public class Skill {

    /** 技能id **/
    private int skillId;
    /** 下次执行技能的回合数 **/
    private long nextRound;


    public void afterSkillExecuted(SkillConfig config, BattleScene scene) {
        nextRound = scene.getSceneRound() + config.getCoolDown() / scene.getRoundPeriod();
    }


    public boolean isCoolDown(long curRound) {
        return nextRound > curRound;
    }


}
