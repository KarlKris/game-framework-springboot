package com.li.battle.skill.handler;

import com.li.battle.ConfigHelper;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.ChannelSkillConfig;
import com.li.battle.resource.SelectorConfig;
import com.li.battle.resource.SkillConfig;
import com.li.battle.selector.SelectParam;
import com.li.battle.selector.SelectorHolder;
import com.li.battle.selector.SelectorResult;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillType;
import com.li.battle.skill.processor.SkillProcessor;
import com.li.battle.skill.processor.SkillProcessorHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
    private SelectorHolder selectorHolder;
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

    @Override
    public SelectorResult select(FightUnit caster, SkillConfig config, SelectParam param) {
        ChannelSkillConfig skillConfig = configHelper.getChannelSkillConfigById(config.getId());
        for (int selectorId : skillConfig.getSelectorIds()) {
            // 返回第一个不为空的结果
            SelectorConfig selectorConfig = configHelper.getSelectorConfigById(selectorId);
            SelectorResult result = selectorHolder.getSelectorByType(selectorConfig.getType())
                    .select(caster, selectorConfig, param, skillConfig.getRange());
            if (!CollectionUtils.isEmpty(result.getResults())) {
                return result;
            }
        }
        return SelectorResult.EMPTY;
    }

    @Override
    public int calculateDurationTime(SkillConfig config) {
        ChannelSkillConfig skillConfig = configHelper.getChannelSkillConfigById(config.getId());
        return skillConfig.getDurationTime();
    }
}
