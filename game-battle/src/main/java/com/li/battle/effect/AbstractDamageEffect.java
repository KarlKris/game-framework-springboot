package com.li.battle.effect;

import com.li.battle.buff.core.BuffModifier;
import com.li.battle.core.context.AbstractDamageAlterContext;
import com.li.battle.skill.BattleSkill;
import com.li.battle.util.ValueAlter;

/**
 * 伤害类效果基类
 * @author li-yuanwen
 * @date 2022/5/24
 */
public abstract class AbstractDamageEffect<B extends BuffModifier> extends EffectAdapter<B> {

    protected void incrementDamage(BattleSkill skill, long incrementValue) {
        incrementDamage0(skill.getContext(), incrementValue);

    }

    protected void incrementDamage(B buff, long incrementValue) {
        incrementDamage0(buff.getContext(), incrementValue);
    }


    protected void incrementDamage0(AbstractDamageAlterContext context, long incrementValue) {
        ValueAlter damageValue = context.getDamageValue();
        if (damageValue == null) {
            context.initDamageValue(new ValueAlter(incrementValue));
        } else {
            damageValue.incrementValue(incrementValue);
        }
    }


}
