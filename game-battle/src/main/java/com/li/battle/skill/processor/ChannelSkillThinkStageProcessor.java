package com.li.battle.skill.processor;

import com.li.battle.resource.ChannelSkillConfig;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillStage;
import org.springframework.stereotype.Component;

/**
 * 持续型技能引导施法阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/20
 */
@Component
public class ChannelSkillThinkStageProcessor extends AbstractSkillStageProcessor<ChannelSkillConfig> {

    @Override
    public SkillStage getSkillStage() {
        return SkillStage.CHANNEL_THINK;
    }

    @Override
    public void process(BattleSkill skill, ChannelSkillConfig config) {
        if (isExecutable(config.getThinkEffects())) {
            process0(skill, config.getThinkEffects());
        }
        long curRound = skill.getScene().getSceneRound();
        int roundPeriod = skill.getScene().getRoundPeriod();
        int channelRound = config.getChannelTime() / roundPeriod;
        int addRound = config.getThinkInterval() / roundPeriod;
        long endRound = skill.getChannelStartRound() + channelRound;
        if (curRound + addRound < endRound) {
            skill.addNextRound(addRound);
            skill.updateSkillStage(SkillStage.CHANNEL_THINK);
        } else {
            skill.addNextRound(endRound - addRound);
            skill.updateSkillStage(SkillStage.CHANNEL_FINISH);
        }
    }
}
