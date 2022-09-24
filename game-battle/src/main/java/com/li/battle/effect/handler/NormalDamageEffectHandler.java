package com.li.battle.effect.handler;

import com.li.battle.effect.EffectType;
import com.li.battle.effect.domain.NormalDamageEffectParam;
import com.li.battle.util.ValueAlter;
import org.springframework.stereotype.Component;

/**
 * 普通伤害效果
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Component
public class NormalDamageEffectHandler extends AbstractDamageEffectHandler<NormalDamageEffectParam> {

    @Override
    public EffectType getType() {
        return EffectType.NORMAL_DAMAGE;
    }

    @Override
    protected ValueAlter newValueAlter(long dmg) {
        return new ValueAlter(dmg);
    }
}
