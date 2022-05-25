package com.li.battle.buff.creator;

import com.li.battle.buff.BuffType;
import com.li.battle.buff.core.Buff;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.BuffConfig;

/**
 * Buff构建器
 * @author li-yuanwen
 * @date 2022/5/25
 */
public interface BuffCreator {


    /**
     * 负责的Buff类型
     * @return Buff类型
     */
    BuffType getType();


    /**
     * 创建特定类型的Buff
     * @param caster buff施加者
     * @param target buff挂载者
     * @param buffConfig buff配置
     * @param skillId buff由哪个技能创建,<=0代表非是由技能创建
     * @return Buff
     */
    Buff newInstance(FightUnit caster, FightUnit target, BuffConfig buffConfig, int skillId);




}
