package com.li.battle.selector;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SelectorConfig;
import com.li.battle.skill.BattleSkill;

/**
 * 目标选择器
 * @author li-yuanwen
 */
public interface Selector {


    /**
     * 目标选择器类型
     * @return 目标选择器类型
     */
    SelectorType getType();


    /**
     * 战斗单位选择目标
     * @param unit 主动选择单位
     * @param config 选择器配置
     * @return 命中目标集
     */
    SelectorResult select(FightUnit unit, SelectorConfig config);


    /**
     * 技能选择目标
     * @param unit 主动选择单位
     * @param config 选择器配置
     * @param skill 技能
     * @return 命中目标集
     */
    SelectorResult select(FightUnit unit, SelectorConfig config, BattleSkill skill);

    /**
     * 技能选择目标
     * @param unit 主动选择单位
     * @param config 选择器配置
     * @param buff buff
     * @return 命中目标集
     */
    SelectorResult select(FightUnit unit, SelectorConfig config, Buff buff);
}
