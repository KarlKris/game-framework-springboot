package com.li.battle.skill.processor;

import com.li.battle.resource.ChannelSkillConfig;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillStage;

/**
 * 持续型技能引导结束阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/20
 */
public class ChannelSkillFinishStageProcessor extends AbstractSkillStageProcessor<ChannelSkillConfig> {

    @Override
    public SkillStage getSkillSatge() {
        return SkillStage.CHANNEL_FINISH;
    }

    @Override
    public void process(BattleSkill skill, ChannelSkillConfig config) {
        if (isExecutable(config.getFinishEffects())) {
            process0(skill, config.getFinishEffects());
        }
    }
}
