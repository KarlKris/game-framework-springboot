package com.li.battle.ai.condition.impl;

import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.ai.condition.AbstractCondition;
import com.li.battle.core.*;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SkillConfig;
import com.li.battle.skill.SkillType;

import java.util.List;

/**
 * 技能不在CD中
 * @author li-yuanwen
 * @date 2022/9/27
 */
public class ConditionIsNotCoolDown extends AbstractCondition {

    @Override
    public boolean valid(BlackBoard board) {
        FightUnit unit = board.getUnit();
        List<Skill> skills = unit.getSkills();
        if (board.getSkillIndex() >= skills.size()) {
            return false;
        }
        Skill skill = skills.get(board.getSkillIndex());
        if (skill.getNextRound() > unit.getScene().getSceneRound()) {
            return false;
        }
        ConfigHelper configHelper = unit.getScene().battleSceneHelper().configHelper();
        SkillConfig config = configHelper.getSkillConfigById(skill.getSkillId());
        return SkillType.belongTo(config.getType(), SkillType.GENERAL_SKILL) || SkillType.belongTo(config.getType(), SkillType.CHANNEL_SKILL);
    }
}
