package com.li.battle.skill.processor;

import com.li.battle.core.UnitState;
import com.li.battle.event.core.SkillExecutedEvent;
import com.li.battle.resource.GeneralSkillConfig;
import com.li.battle.skill.*;
import org.springframework.stereotype.Component;

/**
 * 一次性技能施法阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/20
 */
@Component
public class GeneralSkillSpellStageProcessor extends AbstractSkillStageProcessor<GeneralSkillConfig> {

    @Override
    public SkillStage getSkillStage() {
        return SkillStage.SPELL;
    }

    @Override
    public void process(BattleSkill skill, GeneralSkillConfig config) {
        skill.getScene().getFightUnit(skill.getCaster()).modifyState(UnitState.BACK);
        skill.addNextRound(config.getBackRockingTime() / skill.getScene().getRoundPeriod());
        if (isExecutable(config.getSpellEffects())) {
            process0(skill, config.getSpellEffects());
        }
        skill.updateSkillStage(SkillStage.FINISH);

        skill.getScene().eventDispatcher().dispatch(new SkillExecutedEvent(skill));
    }
}
