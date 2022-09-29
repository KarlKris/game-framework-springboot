package com.li.battle.effect.domain;

import com.li.battle.effect.*;
import lombok.Getter;

/**
 * buff效果参数
 * @author li-yuanwen
 * @date 2022/9/22
 */
@Getter
public class AddBuffEffectParam implements EffectParam {

    /** 添加的buffId **/
    private int buffId;

    @Override
    public EffectType getType() {
        return EffectType.ADD_BUFF;
    }
}
