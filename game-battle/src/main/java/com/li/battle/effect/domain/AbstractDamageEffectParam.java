package com.li.battle.effect.domain;

import com.li.battle.harm.HarmType;
import lombok.Getter;

/**
 * 伤害类效果参数基类
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Getter
public abstract class AbstractDamageEffectParam implements EffectParam {


    /**
     * 伤害类型
     * @return HarmType
     */
    public abstract HarmType harmType();
}
