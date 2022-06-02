package com.li.battle.skill.processor;

import com.li.battle.resource.GeneralSkillConfig;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillStage;
import org.springframework.stereotype.Component;

/**
 * 一次性技能施法阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/20
 */
@Component
public class GeneralSkillSpellProcessor extends AbstractSkillProcessor<GeneralSkillConfig> {

    @Override
    public SkillStage getSkillType() {
        return SkillStage.SPELL;
    }

    @Override
    public void process(BattleSkill skill, GeneralSkillConfig config) {
        if (isExecutable(config.getSpellEffects())) {
            process0(skill, config.getSpellEffects());
        }
    }
}
