package com.li.battle.skill.handler;

import com.li.battle.ConfigHelper;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.GeneralSkillConfig;
import com.li.battle.resource.SelectorConfig;
import com.li.battle.resource.SkillConfig;
import com.li.battle.selector.SelectParam;
import com.li.battle.selector.SelectorHolder;
import com.li.battle.selector.SelectorResult;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillType;
import com.li.battle.skill.processor.SkillProcessor;
import com.li.battle.skill.processor.SkillProcessorHolder;
import com.li.common.resource.anno.ResourceInject;
import com.li.common.resource.storage.ResourceStorage;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * 一次性技能执行器
 * @author li-yuanwen
 * @date 2022/5/19
 */
@Component
public class GeneralSkillHandler implements SkillHandler {

    @ResourceInject
    private ResourceStorage<Integer, GeneralSkillConfig> storage;

    @Resource
    private SkillProcessorHolder skillProcessorHolder;
    @Resource
    private ConfigHelper configHelper;
    @Resource
    private SelectorHolder selectorHolder;


    @Override
    public SkillType getSkillType() {
        return SkillType.GENERAL_SKILL;
    }

    @Override
    public void handle(BattleSkill skill) {
        GeneralSkillConfig skillConfig = storage.getResource(skill.getSkillId());
        SkillProcessor<GeneralSkillConfig> skillProcessor
                = (SkillProcessor<GeneralSkillConfig>) skillProcessorHolder.getSkillProcessor(skill.getNextStage());
        skillProcessor.process(skill, skillConfig);
    }

    @Override
    public SelectorResult select(FightUnit caster, SkillConfig config, SelectParam param) {
        GeneralSkillConfig skillConfig = storage.getResource(config.getId());
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
        GeneralSkillConfig skillConfig = storage.getResource(config.getId());
        return skillConfig.getDurationTime();
    }
}
