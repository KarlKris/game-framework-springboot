package com.li.battle.effect.domain;

import com.li.battle.effect.EffectType;
import lombok.ToString;

/**
 * 移除剩余护盾参数
 * @author li-yuanwen
 * @date 2022/9/26
 */
@ToString
public class RemoveRemainShieldEffectParam implements EffectParam {

    @Override
    public EffectType getType() {
        return EffectType.REMOVE_REMAIN_SHIELD;
    }
}
