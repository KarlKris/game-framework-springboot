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
        @JsonSubTypes.Type(value = NormalDamageEffectParam.class, name = EffectParam.NORMAL_DAMAGE),
        @JsonSubTypes.Type(value = ProjectileEffectParam.class, name = EffectParam.PROJECTILE),
        @JsonSubTypes.Type(value = AddShieldEffectParam.class, name = EffectParam.ADD_SHIELD),
        @JsonSubTypes.Type(value = RemoveRemainShieldEffectParam.class, name = EffectParam.REMOVE_REMAIN_SHIELD),
        @JsonSubTypes.Type(value = ModifyAttributeEffectParam.class, name = EffectParam.MODIFY_ATTRIBUTE),
})
public interface EffectParam {

    String TRIGGER = "TRIGGER";
    String ADD_BUFF = "ADD_BUFF";
    String NORMAL_DAMAGE = "NORMAL_DAMAGE";
    String PROJECTILE = "PROJECTILE";
    String ADD_SHIELD = "ADD_SHIELD";
    String REMOVE_REMAIN_SHIELD = "REMOVE_REMAIN_SHIELD";
    String MODIFY_ATTRIBUTE = "MODIFY_ATTRIBUTE";


    /**
     * 效果类型
     * @return 效果类型
     */
    EffectType getType();

}
