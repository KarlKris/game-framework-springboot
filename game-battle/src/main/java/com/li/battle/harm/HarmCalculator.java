package com.li.battle.harm;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.domain.AbstractDamageEffectParam;

/**
 * 伤害计算器
 * @author li-yuanwen
 * @date 2022/9/23
 */
public interface HarmCalculator<EP extends AbstractDamageEffectParam> {

    /**
     * 伤害类型
     * @return HarmType
     */
    HarmType getType();

    /**
     * 计算伤害值
     * @param attacker 攻击方
     * @param defender 防守方
     * @param param 伤害参数
     * @return 伤害值
     */
    long calculate(FightUnit attacker, FightUnit defender, EP param);

}
