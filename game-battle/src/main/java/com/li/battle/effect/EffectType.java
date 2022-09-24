package com.li.battle.effect;

import lombok.Getter;

/**
 * 效果类型
 * @author li-yuanwen
 * @date 2022/9/22
 */
@Getter
public enum EffectType {

    /** 添加Buff效果 **/
    ADD_BUFF(1),
    /** 创建子弹效果 **/
    PROJECTILE(2),
    /** 创建触发器 **/
    TRIGGER(3),
    /** 普通伤害类效果 **/
    NORMAL_DAMAGE(4),

    ;

    private final int code;

    EffectType(int code) {
        this.code = code;
    }

}
