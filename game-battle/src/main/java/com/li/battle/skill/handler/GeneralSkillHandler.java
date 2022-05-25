package com.li.battle.skill.handler;

import com.li.battle.resource.GeneralSkillConfig;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillType;
import com.li.battle.skill.processor.SkillProcessor;
import com.li.battle.skill.processor.SkillProcessorHolder;
import com.li.common.resource.anno.ResourceInject;
import com.li.common.resource.storage.ResourceStorage;
import org.springframework.stereotype.Component;

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
}
