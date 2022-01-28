package com.li.battle.skill.processor;

/**
 * 开关类技能效果执行器
 * @author li-yuanwen
 */
public interface ToggleSkillAbilityProcessor {

    /** 技能开启 **/
    void onAbilityToggleOn();

    /** 技能关闭 **/
    void onAbilityToggleOff();


}
