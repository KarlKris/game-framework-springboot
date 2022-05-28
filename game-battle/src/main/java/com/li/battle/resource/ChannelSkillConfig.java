package com.li.battle.resource;

import com.li.battle.buff.core.Buff;
import com.li.battle.effect.Effect;
import lombok.Getter;

import java.util.List;

/**
 * 持续施法类技能配置
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Getter
public class ChannelSkillConfig {

    /** 技能id **/
    private int id;
    /** 技能选择目标选择集 **/
    private List<Integer> selectorIds;
    /** 技能起手阶段效果 **/
    private Effect<Buff>[] initEffects;
    /** 前摇时长(毫秒) **/
    private int frontRockingTime;
    /** 前摇阶段是否可打断 **/
    private boolean frontInterrupted;

    /** 引导开始阶段效果 **/
    private Effect<Buff>[] startEffects;
    /** 引导施法阶段效果 **/
    private Effect<Buff>[] thinkEffects;
    /** 引导结束阶段效果 **/
    private Effect<Buff>[] finishEffects;
    /** 引导施法触发间隔(毫秒) **/
    private int thinkInterval;
    /** 引导总时长(毫秒) **/
    private int channelTime;

    /** 后摇时长(毫秒) **/
    private int backRockingTime;
    /** 后摇阶段是否可打断 **/
    private boolean backInterrupted;
    /** 结束阶段效果 **/
    private Effect<Buff>[] destroyEffects;

}
