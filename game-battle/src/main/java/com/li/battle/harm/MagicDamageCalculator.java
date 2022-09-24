package com.li.battle.harm;

import com.li.battle.resource.FormulaConfig;
import com.li.common.resource.anno.ResourceInject;
import org.springframework.stereotype.Component;

/**
 * 法术伤害计算器
 * @author li-yuanwen
 * @date 2022/9/24
 */
@Component
public class MagicDamageCalculator extends AbstractNormalHarmCalculator {

    @ResourceInject(key = "MAGIC_DAMAGE", type = FormulaConfig.class)
    private String magicDamageExpression;

    @Override
    public HarmType getType() {
        return HarmType.MAGIC;
    }

    @Override
    protected String getExpression() {
        return magicDamageExpression;
    }
}
