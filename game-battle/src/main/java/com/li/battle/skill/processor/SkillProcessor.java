package com.li.battle.skill.processor;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.skill.model.BattleSkill;
import com.li.battle.skill.model.SkillStage;

/**
 * 技能阶段执行器
 * @author li-yuanwen
 * @date 2022/5/19
 */
public interface SkillProcessor<T> {


    /**
     * 负责的技能阶段
     * @return 技能阶段
     */
    SkillStage getSkillType();


    /**
     * 执行技能效果
     * @param skill 技能
     * @param scene 战斗场景
     * @param config 技能配置
     */
    void process(BattleSkill skill, BattleScene scene, T config);

}
