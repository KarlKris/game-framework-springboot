package com.li.battle.effect.domain;

import com.li.battle.effect.EffectType;
import com.li.battle.harm.HarmType;
import lombok.Getter;
import lombok.ToString;

/**
 * 普通伤害效果
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Getter
@ToString
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
    public HarmType getHarmType() {
        return t;
    }

    @Override
    public EffectType getType() {
        return EffectType.NORMAL_DAMAGE;
    }
}
