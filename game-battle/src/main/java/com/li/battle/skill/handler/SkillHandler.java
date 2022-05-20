package com.li.battle.skill.handler;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.skill.model.BattleSkill;
import com.li.battle.skill.model.SkillType;

/**
 * 指定技能类型的执行器
 * @author li-yuanwen
 * @date 2022/5/19
 */
public interface SkillHandler {


    /**
     * 负责的技能类型
     * @return 技能类型
     */
    SkillType getSkillType();


    /**
     * 处理技能效果
     * @param skill 技能
     * @param scene 战斗场景
     */
    void handle(BattleSkill skill, BattleScene scene);

}
