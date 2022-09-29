package com.li.battle.event.core;

/**
 * 战斗事件类型枚举
 * @author li-yuanwen
 * @date 2022/5/18
 */
public enum BattleEventType {

    /** Buff生效前(还未加入Buff容器) **/
    BEFORE_BUFF_AWAKE,

    /** 某个主动技能执行成功事件 **/
    SKILL_EXECUTED,

    /** 造成伤害前事件 **/
    BEFORE_DAMAGE,

    /** 造成伤害后事件 **/
    AFTER_DAMAGE,

    /** 死亡前事件 **/
    BEFORE_DEAD,

    /** 击杀事件 **/
    KILL,

    /** 移动事件 **/
    MOVE,

    ;

}
