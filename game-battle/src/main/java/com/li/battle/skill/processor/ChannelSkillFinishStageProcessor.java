package com.li.battle.skill.processor;

import com.li.battle.core.UnitState;
import com.li.battle.resource.ChannelSkillConfig;
import com.li.battle.skill.*;

/**
 * 持续型技能引导结束阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/20
 */
public class ChannelSkillFinishStageProcessor extends AbstractSkillStageProcessor<ChannelSkillConfig> {

    @Override
    public SkillStage getSkillStage() {
        return SkillStage.CHANNEL_FINISH;
    }

    @Override
    public void process(BattleSkill skill, ChannelSkillConfig config) {
        if (isExecutable(config.getFinishEffects())) {
            process0(skill, config.getFinishEffects());
        }
        skill.updateSkillStage(SkillStage.CHANNEL_DESTROY);
        skill.addNextRound(config.getBackRockingTime() / skill.getScene().getRoundPeriod());
        skill.getScene().getFightUnit(skill.getCaster()).modifyState(UnitState.BACK);
    }
}
