package com.li.battle.skill.processor;

/**
 * 技能效果执行器接口
 * @author li-yuanwen
 */
public interface SkillAbilityProcessor {

    /**
     * 技能释放前置条件检查
     * @return true 能执行
     */
    boolean onBeforeAbilityStart();

    /** 技能起手 **/
    void onAbilityStart();

    /** 施法 **/
    void spell();

    /** 结束 **/
    void finish();


}
