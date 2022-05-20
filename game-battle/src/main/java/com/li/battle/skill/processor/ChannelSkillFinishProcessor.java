package com.li.battle.skill.processor;

import com.li.battle.config.ChannelSkillConfig;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.effect.Effect;
import com.li.battle.skill.model.BattleSkill;
import com.li.battle.skill.model.SkillStage;

/**
 * 持续型技能引导结束阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/20
 */
public class ChannelSkillFinishProcessor implements SkillProcessor<ChannelSkillConfig> {

    @Override
    public SkillStage getSkillType() {
        return SkillStage.CHANNEL_FINISH;
    }

    @Override
    public void process(BattleSkill skill, BattleScene scene, ChannelSkillConfig config) {
        for (Effect effect : config.getFinishEffects()) {
            effect.onAction();
        }
    }
}
