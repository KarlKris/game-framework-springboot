package com.li.battle.effect.domain;

import com.li.battle.core.Attribute;
import com.li.battle.effect.EffectType;
import lombok.*;

/**
 * 修改属性效果参数
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Getter
@ToString
public class ModifyAttributeEffectParam implements EffectParam {

    /** 属性 **/
    private Attribute attr;
    /** 增加值 **/
    private int add;
    /** 是否是己方 **/
    private boolean self;

    @Override
    public EffectType getType() {
        return EffectType.MODIFY_ATTRIBUTE;
    }
}
