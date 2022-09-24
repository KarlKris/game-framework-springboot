package com.li.battle.harm;

import cn.hutool.core.util.RandomUtil;
import com.li.battle.core.*;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.domain.NormalDamageEffectParam;
import com.li.common.js.ExpressionHelper;

/**
 * 普通伤害计算器抽象
 * @author li-yuanwen
 * @date 2022/9/24
 */
public abstract class AbstractNormalHarmCalculator implements HarmCalculator<NormalDamageEffectParam> {

    @Override
    public long calculate(FightUnit attacker, FightUnit defender, NormalDamageEffectParam param) {
        long physicsInc = attacker.getAttributeValue(Attribute.PHYSICAL_ATTACK) * param.getP() / Const.TEN_THOUSAND;
        long magicInc = attacker.getAttributeValue(Attribute.MAGIC_STRENGTH) * param.getM() / Const.TEN_THOUSAND;
        long dmg = param.getB() + physicsInc + magicInc;

        // 暴击
        int crit = (int) attacker.getAttributeValue(Attribute.CRIT);
        if (RandomUtil.randomInt(Const.HUNDRED) <= crit) {
            dmg = dmg * (int) attacker.getAttributeValue(Attribute.CRITIC_DAMAGE) / Const.HUNDRED;
        }

        return ExpressionHelper.invoke(getExpression(), Long.class, new HarmContext(dmg, attacker, defender));
    }

    /**
     * 获取计算公式表达式
     * @return 计算公式表达式
     */
    protected abstract String getExpression();

}
