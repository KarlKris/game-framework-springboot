package com.li.battle.skill.processor;

import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillStage;

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
     * @param config 技能配置
     */
    void process(BattleSkill skill, T config);

}
