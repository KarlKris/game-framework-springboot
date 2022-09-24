package com.li.battle.effect.domain;

import com.li.battle.effect.*;
import lombok.Getter;

/**
 * 创建触发器效果参数
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Getter
public class TriggerEffectParam implements EffectParam {

    /** 触发器标识 **/
    private int triggerId;

    @Override
    public EffectType type() {
        return EffectType.TRIGGER;
    }
}
