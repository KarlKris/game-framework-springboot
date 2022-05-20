package com.li.battle.buff.core;

/**
 * buff监听器(后续进行更细腻的拆分)
 * @author li-yuanwen
 */
public interface BuffMonitor {

    /** 监听Buff生效前 **/
    void onBuffAwake();

    /** 监听某个主动技能执行成功 **/
    void onAbilityExecuted();

    /** 监听我方给目标造成伤害前触发 **/
    void onBeforeGiveDamage();

    /** 监听我方给目标造成伤害后触发 **/
    void onAfterGiveDamage();

    /** 监听我方受到伤害前触发 **/
    void onBeforeTakeDamage();

    /**  监听我方受到伤害后触发 **/
    void onAfterTakeDamage();

    /** 监听我方死亡前触发 **/
    void onBeforeDead();

    /** 监听我方死亡后触发 **/
    void onAfterDead();

    /** 监听我方击杀目标时触发 **/
    void onKill();

}
