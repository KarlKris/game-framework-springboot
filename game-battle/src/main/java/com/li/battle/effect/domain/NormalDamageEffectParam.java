package com.li.battle.effect.domain;

import com.li.battle.effect.EffectType;
import com.li.battle.harm.HarmType;
import lombok.Getter;

/**
 * 普通伤害效果
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Getter
public class NormalDamageEffectParam extends AbstractDamageEffectParam {

    /** 基础伤害 **/
    private int b;
    /** 法强加成万分比 **/
    private int m;
    /** 物攻加成万分比 **/
    private int p;
    /** 伤害类型(物理,法术) **/
    private HarmType t;

    @Override
    public HarmType harmType() {
        return t;
    }

    @Override
    public EffectType type() {
        return EffectType.NORMAL_DAMAGE;
    }
}
