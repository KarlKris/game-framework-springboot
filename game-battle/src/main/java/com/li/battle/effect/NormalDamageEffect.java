package com.li.battle.effect;

import com.li.battle.buff.core.BuffModifier;
import com.li.battle.skill.BattleSkill;
import org.springframework.stereotype.Component;

/**
 * 伤害类效果
 * @author li-yuanwen
 * @date 2022/5/27
 */
@Component
public class NormalDamageEffect extends AbstractDamageEffect<BuffModifier> {

    /** 基础伤害 **/
    private int baseDamage;
    /** 物攻加成 **/
    private int physicalAttackPct;
    /** 法强加成 **/
    private int spellStrengthPct;

    @Override
    public void onAction(BattleSkill skill) {
        // todo
    }

    @Override
    public void onAction(BuffModifier buff) {
        // todo
    }
}
