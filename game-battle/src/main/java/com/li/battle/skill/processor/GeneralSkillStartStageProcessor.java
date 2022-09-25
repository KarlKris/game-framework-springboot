package com.li.battle.skill.processor;

import com.li.battle.resource.GeneralSkillConfig;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillStage;
import org.springframework.stereotype.Component;

/**
 * 一次性技能起手阶段技能效果执行器
 * @author li-yuanwen
 * @date 2022/5/19
 */
@Component
public class GeneralSkillStartStageProcessor extends AbstractSkillStageProcessor<GeneralSkillConfig> {


    @Override
    public SkillStage getSkillSatge() {
        return SkillStage.START;
    }

    @Override
    public void process(BattleSkill skill, GeneralSkillConfig config) {
        if (isExecutable(config.getStartEffects())) {
            process0(skill, config.getStartEffects());
            // 技能进CD
            makeSkillStartCoolDown(skill);
        }
    }
}
