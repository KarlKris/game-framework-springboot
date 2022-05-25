package com.li.battle.core;

/**
 * 单元状态
 * @author li-yuanwen
 * @date 2021/10/16
 */
public enum UnitState {

    /** 正常 **/
    NORMAL,

    /** 眩晕-目标不再响应任何操控 **/
    STUN,

    /** 缠绕-又称定身——目标不响应移动请求，但是可以执行某些操作，如施放某些技能 **/
    ROOT,

    /** 沉默-目标禁止施放技能 **/
    SILENCE,

    /** 无敌-几乎不受到所有的伤害和效果影响 **/
    INVINCIBLE,

    /** 隐身-不可被其他人看见 **/
    INVISIBLE,

    /** 击飞-类眩晕，不再响应任何操作 **/
    KNOCK,

    ;
}
