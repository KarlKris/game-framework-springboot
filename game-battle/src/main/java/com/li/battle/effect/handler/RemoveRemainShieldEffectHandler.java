package com.li.battle.effect.handler;

import com.li.battle.buff.BuffContext;
import com.li.battle.core.Attribute;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.EffectType;
import com.li.battle.effect.domain.*;
import com.li.battle.effect.source.BuffEffectSource;
import org.springframework.stereotype.Component;

/**
 * 移除剩余未使用完的护盾效果
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Component
public class RemoveRemainShieldEffectHandler extends AbstractEffectHandler<BuffEffectSource, EffectParam> {

    @Override
    public EffectType getType() {
        return EffectType.REMOVE_REMAIN_SHIELD;
    }

    @Override
    protected void execute0(BuffEffectSource source, EffectParam effectParam) {
        BuffContext buffContext = source.getBuff().buffContext();
        int shieldValue = buffContext.getShieldValue();
        if (shieldValue > 0) {
            FightUnit unit = source.getCaster();
            unit.modifyAttribute(Attribute.SHIELD, - (long) shieldValue);
        }
    }
}
