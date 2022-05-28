package com.li.battle.skill.handler;

import com.li.battle.ConfigHelper;
import com.li.battle.resource.ChannelSkillConfig;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillType;
import com.li.battle.skill.processor.SkillProcessor;
import com.li.battle.skill.processor.SkillProcessorHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 持续性技能效果处理器
 * @author li-yuanwen
 * @date 2022/5/19
 */
@Component
public class ChannelSkillHandler implements SkillHandler {

    @Resource
    private ConfigHelper configHelper;
    @Resource
    private SkillProcessorHolder skillProcessorHolder;

    @Override
    public SkillType getSkillType() {
        return SkillType.CHANNEL_SKILL;
    }

    @Override
    public void handle(BattleSkill skill) {
        ChannelSkillConfig skillConfig = configHelper.getChannelSkillConfigById(skill.getSkillId());
        SkillProcessor<ChannelSkillConfig> skillProcessor
                = (SkillProcessor<ChannelSkillConfig>) skillProcessorHolder.getSkillProcessor(skill.getNextStage());
        skillProcessor.process(skill, skillConfig);
    }
}
