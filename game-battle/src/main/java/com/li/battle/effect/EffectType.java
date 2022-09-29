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
    ADD_BUFF,
    /** 创建子弹效果 **/
    PROJECTILE,
    /** 创建触发器 **/
    TRIGGER,
    /** 普通伤害类效果 **/
    NORMAL_DAMAGE,
    /** 添加护盾 **/
    ADD_SHIELD,
    /** 移除剩余护盾 **/
    REMOVE_REMAIN_SHIELD,
    /** 修改属性 **/
    MODIFY_ATTRIBUTE,

    ;


}
