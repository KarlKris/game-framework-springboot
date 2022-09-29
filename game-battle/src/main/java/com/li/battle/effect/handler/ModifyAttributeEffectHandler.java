package com.li.battle.effect.handler;

import com.li.battle.core.unit.*;
import com.li.battle.effect.EffectType;
import com.li.battle.effect.domain.ModifyAttributeEffectParam;
import com.li.battle.effect.source.EffectSource;
import org.springframework.stereotype.Component;

/**
 * 修改属性效果
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Component
public class ModifyAttributeEffectHandler extends AbstractEffectParamHandler<ModifyAttributeEffectParam> {

    @Override
    public EffectType getType() {
        return EffectType.MODIFY_ATTRIBUTE;
    }

    @Override
    protected void execute0(EffectSource source, ModifyAttributeEffectParam effectParam) {
        if (effectParam.isSelf()) {
            FightUnit unit = source.getCaster();
            unit.modifyAttribute(effectParam.getAttr(), (long) effectParam.getAdd());
        } else {
            for (FightUnit unit : source.getTargetUnits()) {
                unit.modifyAttribute(effectParam.getAttr(), (long) effectParam.getAdd());
            }
        }
    }
}
