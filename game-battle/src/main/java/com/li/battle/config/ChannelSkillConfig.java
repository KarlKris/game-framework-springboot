package com.li.battle.config;

import com.li.battle.selector.core.SelectorType;
import com.li.battle.effect.Effect;
import lombok.Getter;

/**
 * 持续施法类技能配置
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Getter
public class ChannelSkillConfig {

    /** 技能id **/
    private int id;
    /** 技能选择类型 **/
    private SelectorType type;
    /** 技能起手阶段效果 **/
    private Effect[] initEffects;
    /** 前摇时长(毫秒) **/
    private int frontRockingTime;
    /** 前摇阶段是否可打断 **/
    private boolean frontInterrupted;

    /** 引导开始阶段效果 **/
    private Effect[] startEffects;
    /** 引导施法阶段效果 **/
    private Effect[] thinkEffects;
    /** 引导结束阶段效果 **/
    private Effect[] finishEffects;
    /** 引导施法触发间隔(毫秒) **/
    private int thinkInterval;
    /** 引导总时长(毫秒) **/
    private int channelTime;

    /** 后摇时长(毫秒) **/
    private int backRockingTime;
    /** 后摇阶段是否可打断 **/
    private boolean backInterrupted;
    /** 结束阶段效果 **/
    private Effect[] destroyEffects;

}
