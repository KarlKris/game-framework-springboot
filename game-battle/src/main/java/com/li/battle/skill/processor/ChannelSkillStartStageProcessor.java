package com.li.battle.skill.processor;

import com.li.battle.resource.ChannelSkillConfig;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillStage;
import org.springframework.stereotype.Component;

/**
 * 持续型技能引导开始阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/20
 */
@Component
public class ChannelSkillStartStageProcessor extends AbstractSkillStageProcessor<ChannelSkillConfig> {

    @Override
    public SkillStage getSkillSatge() {
        return SkillStage.CHANNEL_START;
    }

    @Override
    public void process(BattleSkill skill, ChannelSkillConfig config) {
        if (isExecutable(config.getStartEffects())) {
            process0(skill, config.getStartEffects());
        }
        skill.updateSkillStage(SkillStage.CHANNEL_THINK);
    }
}
