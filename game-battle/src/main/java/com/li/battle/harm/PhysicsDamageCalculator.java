package com.li.battle.harm;

import com.li.battle.resource.FormulaConfig;
import com.li.common.resource.anno.ResourceInject;
import org.springframework.stereotype.Component;

/**
 * 物理伤害计算器
 * @author li-yuanwen
 * @date 2022/9/24
 */
@Component
public class PhysicsDamageCalculator extends AbstractNormalHarmCalculator {

    @ResourceInject(key = "PHYSICS_DAMAGE", type = FormulaConfig.class)
    private String physicsDamageExpression;

    @Override
    public HarmType getType() {
        return HarmType.PHYSICS;
    }

    @Override
    protected String getExpression() {
        return physicsDamageExpression;
    }

}
