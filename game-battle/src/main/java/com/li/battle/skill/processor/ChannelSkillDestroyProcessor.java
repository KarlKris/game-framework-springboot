package com.li.battle.skill.processor;

import com.li.battle.config.ChannelSkillConfig;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.effect.Effect;
import com.li.battle.skill.model.BattleSkill;
import com.li.battle.skill.model.SkillStage;
import org.springframework.stereotype.Component;

/**
 * 持续型技能销毁阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/20
 */
@Component
public class ChannelSkillDestroyProcessor implements SkillProcessor<ChannelSkillConfig> {

    @Override
    public SkillStage getSkillType() {
        return SkillStage.CHANNEL_DESTROY;
    }

    @Override
    public void process(BattleSkill skill, BattleScene scene, ChannelSkillConfig config) {
        for (Effect effect : config.getDestroyEffects()) {
            effect.onAction();
        }
    }
}
