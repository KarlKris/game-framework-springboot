package com.li.battle.skill.processor;

import com.li.battle.resource.ChannelSkillConfig;
import com.li.battle.effect.Effect;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillStage;
import org.springframework.stereotype.Component;

/**
 * 持续型技能引导施法阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/20
 */
@Component
public class ChannelSkillThinkProcessor implements SkillProcessor<ChannelSkillConfig> {

    @Override
    public SkillStage getSkillType() {
        return SkillStage.CHANNEL_THINK;
    }

    @Override
    public void process(BattleSkill skill, ChannelSkillConfig config) {
        for (Effect effect : config.getThinkEffects()) {
            effect.onAction(skill);
        }
    }
}
