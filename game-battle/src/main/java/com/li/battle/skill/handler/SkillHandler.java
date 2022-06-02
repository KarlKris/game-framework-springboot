package com.li.battle.skill.handler;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SkillConfig;
import com.li.battle.selector.SelectParam;
import com.li.battle.selector.SelectorResult;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillType;

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
     */
    void handle(BattleSkill skill);


    /**
     * 根据技能的选择配置选择的目标
     * @param caster 技能施法方
     * @param config 技能配置
     * @param param 选择参数
     * @return 选择结果
     */
    SelectorResult select(FightUnit caster, SkillConfig config, SelectParam param);

    /**
     * 计算某个技能的持续时间
     * @param config 技能配置
     * @return 持续时间
     */
    int calculateDurationTime(SkillConfig config);

}
