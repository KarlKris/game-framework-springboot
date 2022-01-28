package com.li.battle.skill.processor;

/**
 * 激活类技能效果执行器
 * @author li-yuanwen
 */
public interface ActivateSkillAbilityProcessor {

    /** 技能激活 **/
    void onAbilityActivateOn();

    /** 技能失效 **/
    void onAbilityDeactivate();

}
