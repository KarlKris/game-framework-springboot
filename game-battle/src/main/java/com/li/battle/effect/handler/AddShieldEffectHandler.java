package com.li.battle.effect.handler;

import com.li.battle.buff.BuffContext;
import com.li.battle.buff.core.*;
import com.li.battle.core.Attribute;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.EffectType;
import com.li.battle.effect.domain.AddShieldEffectParam;
import com.li.battle.effect.source.*;
import org.springframework.stereotype.Component;

/**
 * 添加护盾效果
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Component
public class AddShieldEffectHandler extends AbstractEffectHandler<BuffEffectSource, AddShieldEffectParam> {

    @Override
    public EffectType getType() {
        return EffectType.ADD_SHIELD;
    }

    @Override
    protected void execute0(BuffEffectSource source, AddShieldEffectParam effectParam) {
        FightUnit caster = source.getCaster();
        Buff buff = source.getBuff();
        BuffContext buffContext = buff.buffContext();
        buffContext.setShieldValue(effectParam.getShieldValue());
        caster.modifyAttribute(Attribute.SHIELD, (long) effectParam.getShieldValue());
    }
}
