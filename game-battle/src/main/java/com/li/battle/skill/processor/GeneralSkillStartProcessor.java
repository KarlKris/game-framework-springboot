package com.li.battle.skill.processor;

import com.li.battle.config.GeneralSkillConfig;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.effect.Effect;
import com.li.battle.skill.model.BattleSkill;
import com.li.battle.skill.model.SkillStage;
import org.springframework.stereotype.Component;

/**
 * 一次性技能起手阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/19
 */
@Component
public class GeneralSkillStartProcessor implements SkillProcessor<GeneralSkillConfig> {


    @Override
    public SkillStage getSkillType() {
        return SkillStage.START;
    }

    @Override
    public void process(BattleSkill skill, BattleScene scene, GeneralSkillConfig config) {
        for (Effect effect : config.getStartEffects()) {
            effect.onAction();
        }
    }
}
