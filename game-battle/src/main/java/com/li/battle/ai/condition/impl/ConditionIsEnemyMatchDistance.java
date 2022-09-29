package com.li.battle.ai.condition.impl;

import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.ai.condition.AbstractCondition;
import com.li.battle.core.*;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SkillConfig;
import com.li.battle.skill.SkillType;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * 条件节点---敌人是否在技能射程内
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class ConditionIsEnemyMatchDistance extends AbstractCondition {

    @Override
    public boolean valid(BlackBoard board) {

        FightUnit unit = board.getUnit();
        BattleScene scene = unit.getScene();

        Skill skill = unit.getSkills().get(board.getSkillIndex());
        ConfigHelper configHelper = scene.battleSceneHelper().configHelper();
        SkillConfig config = configHelper.getSkillConfigById(skill.getSkillId());
        int range = 0;
        if (SkillType.belongTo(config.getType(), SkillType.GENERAL_SKILL)) {
            range = configHelper.getGeneralSkillConfigById(config.getId()).getRange();
        } else {
            range = configHelper.getChannelSkillConfigById(config.getId()).getRange();
        }

        FightUnit enemy = board.getEnemy();
        double distance = enemy.getPosition().distance(unit.getPosition());
        if (distance > range + unit.getRadius() + enemy.getRadius()) {
            Vector2D point = enemy.getPosition().subtract(unit.getPosition()).scalarMultiply(0.3D).add(unit.getPosition());
            board.setPoint(point);
            return false;
        }

        return true;
    }
}
