package com.li.battle.skill.processor;

import com.li.battle.core.UnitState;
import com.li.battle.resource.ChannelSkillConfig;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillStage;
import org.springframework.stereotype.Component;

/**
 * 持续型技能起手阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/20
 */
@Component
public class ChannelSkillInitStageProcessor extends AbstractSkillStageProcessor<ChannelSkillConfig> {

    @Override
    public SkillStage getSkillStage() {
        return SkillStage.CHANNEL_INIT;
    }

    @Override
    public void process(BattleSkill skill, ChannelSkillConfig config) {
        // 技能进CD
        makeSkillStartCoolDown(skill);
        if (isExecutable(config.getInitEffects())) {
            process0(skill, config.getInitEffects());
        }
        skill.getScene().getFightUnit(skill.getCaster()).modifyState(UnitState.FRONT);
        skill.updateSkillStage(SkillStage.CHANNEL_START);
        skill.addNextRound(config.getFrontRockingTime() / skill.getScene().getRoundPeriod());
    }
}
