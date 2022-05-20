package com.li.battle.skill.model;

/**
 * 技能执行阶段
 * @author li-yuanwen
 * @date 2022/5/17
 */
public enum SkillStage {

    // 一次性施法效果技能

    /** 技能起手阶段 **/
    START,

    /** 施法阶段 **/
    SPELL,

    /** 技能结束阶段 **/
    FINISH,

    // 持续施法类技能

    /** 技能起手阶段 **/
    CHANNEL_INIT,

    /** 引导开始阶段 **/
    CHANNEL_START,

    /** 引导施法阶段 **/
    CHANNEL_THINK,

    /** 引导结束阶段 **/
    CHANNEL_FINISH,

    /** 技能结束阶段 **/
    CHANNEL_DESTROY,


    ;


}
