package com.li.battle.effect.source;

import com.li.battle.util.*;
import org.springframework.lang.Nullable;

/**
 * 抽象效果源数据
 * @author li-yuanwen
 * @date 2022/9/23
 */
public abstract class AbstractEffectSource implements EffectSource {

    /** 属性变更值 **/
    @Nullable
    private AttributeValueAlter attributeValue;
    /** 伤害值 **/
    @Nullable
    private ValueAlter damageValue;

    @Override
    public void initDamageValue(ValueAlter damageValue) {
        this.damageValue = damageValue;
    }

    @Override
    public ValueAlter getDamageValue() {
        return damageValue;
    }

    @Override
    public AttributeValueAlter getAttributeValueAlter() {
        return attributeValue;
    }

    @Override
    public void initAttributeValue(AttributeValueAlter attributeValue) {
        this.attributeValue = attributeValue;
    }

}
