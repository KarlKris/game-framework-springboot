package com.li.battle.skill.handler;

import com.li.battle.config.ChannelSkillConfig;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.skill.model.BattleSkill;
import com.li.battle.skill.model.SkillType;
import com.li.battle.skill.processor.SkillProcessor;
import com.li.battle.skill.processor.SkillProcessorHolder;
import com.li.common.resource.anno.ResourceInject;
import com.li.common.resource.storage.ResourceStorage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 持续性技能效果处理器
 * @author li-yuanwen
 * @date 2022/5/19
 */
@Component
public class ChannelSkillHandler implements SkillHandler {

    @ResourceInject
    private ResourceStorage<Integer, ChannelSkillConfig> storage;

    @Resource
    private SkillProcessorHolder skillProcessorHolder;

    @Override
    public SkillType getSkillType() {
        return SkillType.CHANNEL_SKILL;
    }

    @Override
    public void handle(BattleSkill skill, BattleScene scene) {
        ChannelSkillConfig skillConfig = storage.getResource(skill.getSkillId());
        SkillProcessor<ChannelSkillConfig> skillProcessor
                = (SkillProcessor<ChannelSkillConfig>) skillProcessorHolder.getSkillProcessor(skill.getNextStage());
        skillProcessor.process(skill, scene, skillConfig);
    }
}
