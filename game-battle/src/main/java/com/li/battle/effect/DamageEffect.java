package com.li.battle.effect;

import com.li.battle.buff.core.BuffModifier;
import com.li.battle.core.HarmType;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.projectile.Projectile;
import com.li.battle.skill.BattleSkill;

import java.util.Collection;

/**
 * 伤害类效果
 * @author li-yuanwen
 * @date 2022/9/13
 */
public class DamageEffect extends EffectAdapter<BuffModifier> {

    /** 基础伤害 **/
    private int b;
    /** 法强加成万分比 **/
    private int s;
    /** 物攻加成万分比 **/
    private int p;
    /** 伤害类型(物理,法术) **/
    private HarmType t;

    @Override
    public void onAction(BattleSkill skill) {
        super.onAction(skill);
    }

    @Override
    public void onAction(BuffModifier buff) {
        super.onAction(buff);
    }

    @Override
    public void onAction(FightUnit caster, Collection<FightUnit> targetList, Projectile projectile) {
        super.onAction(caster, targetList, projectile);
    }
}
