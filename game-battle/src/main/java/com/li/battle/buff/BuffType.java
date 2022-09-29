package com.li.battle.buff;

import lombok.Getter;

/**
 * buff类型
 * @author li-yuanwen
 * @date 2022/5/19
 */
@Getter
public enum BuffType {

    /** 修改属性Buff **/
    MODIFY_ATTRIBUTE,

    /** 伤害Buff **/
    DAMAGE,

    /** 护盾buff **/
    SHIELD,

    /** 创建子弹 **/
    PROJECTILE,

    ;
}
