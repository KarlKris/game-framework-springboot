package com.li.battle.skill.processor;

/**
 * 持续性触发效果技能执行器
 * @author li-yuanwen
 */
public interface ChannelSkillAbilityProcessor extends SkillAbilityProcessor {

    /** 引导开始 **/
    void channelStart();

    /** 引导技能持续触发，间隔固定时间触发 **/
    void channelThink();

    /** 引导结束 **/
    void channelFinish();

}
