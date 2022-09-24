package com.li.battle.effect.domain;

import com.fasterxml.jackson.annotation.*;
import com.li.battle.effect.EffectType;

/**
 * 效果
 * @author li-yuanwen
 * @date 2022/5/17
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TriggerEffectParam.class, name = EffectParam.TRIGGER),
        @JsonSubTypes.Type(value = AddBuffEffectParam.class, name = EffectParam.ADD_BUFF),
        @JsonSubTypes.Type(value = AbstractDamageEffectParam.class, name = EffectParam.DAMAGE),
        @JsonSubTypes.Type(value = ProjectileEffectParam.class, name = EffectParam.PROJECTILE),
})
public interface EffectParam {


    String TRIGGER = "TRIGGER";
    String ADD_BUFF = "ADD_BUFF";
    String DAMAGE = "DAMAGE";
    String PROJECTILE = "PROJECTILE";


    /**
     * 效果类型
     * @return 效果类型
     */
    EffectType type();

}
