package com.li.battle.skill.config;

import com.li.battle.skill.model.SkillType;
import lombok.Getter;

/**
 * 主动技能--持续性触发效果技能表
 * @author li-yuanwen
 * @date 2021/10/19
 */
@Getter
public class ChannelSkillSetting extends AbstractSkillSetting {

    /** 引导开始阶段---效果表#id **/
    private int channelStartEffectId;

    /** 引导技能持续触发--效果表#id **/
    private int channelThinkEffectId;

    /** 引导结束阶段---效果表#id **/
    private int channelFinishEffectId;

    /** 引导触发间隔(毫秒) **/
    private int thinkInterval;

    /** 引导阶段总时长(毫秒) **/
    private int channelTime;

    @Override
    public SkillType getType() {
        return SkillType.CHANNEL_ABILITY;
    }
}
