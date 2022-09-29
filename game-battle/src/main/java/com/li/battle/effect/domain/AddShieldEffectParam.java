package com.li.battle.effect.domain;

import com.li.battle.effect.EffectType;
import lombok.Getter;

/**
 * 添加护盾
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Getter
public class AddShieldEffectParam implements EffectParam {

    /** 添加的护盾值 **/
    private int shieldValue;

    @Override
    public EffectType getType() {
        return EffectType.ADD_SHIELD;
    }
}
