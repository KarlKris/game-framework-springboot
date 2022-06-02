package com.li.battle.selector;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SelectorConfig;

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
     * @param param 目标选择相关参数
     * @param range 技能施法范围 or 0
     * @return 命中目标集
     */
    SelectorResult select(FightUnit unit, SelectorConfig config, SelectParam param, int range);

}
