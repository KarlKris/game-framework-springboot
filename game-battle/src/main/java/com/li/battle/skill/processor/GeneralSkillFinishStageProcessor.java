package com.li.battle.skill.processor;

import com.li.battle.core.UnitState;
import com.li.battle.resource.GeneralSkillConfig;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillStage;
import org.springframework.stereotype.Component;

/**
 * 一次性技能结束阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/20
 */
@Component
public class GeneralSkillFinishStageProcessor extends AbstractSkillStageProcessor<GeneralSkillConfig> {

    @Override
    public SkillStage getSkillStage() {
        return SkillStage.FINISH;
    }

    @Override
    public void process(BattleSkill skill, GeneralSkillConfig config) {
        skill.getScene().getFightUnit(skill.getCaster()).modifyState(UnitState.NORMAL);
        if (isExecutable(config.getFinishEffects())) {
            process0(skill, config.getFinishEffects());
        }
        skill.addNextRound(skill.getExpireRound() + 1);
    }
}
