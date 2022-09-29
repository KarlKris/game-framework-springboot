package com.li.battle.ai.action.impl;

import com.li.battle.ai.Status;
import com.li.battle.ai.action.AbstractAction;
import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.collision.Circle;
import com.li.battle.core.*;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SkillConfig;
import com.li.battle.skill.SkillType;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 选择敌人
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class SelectEnemyAction extends AbstractAction {

    @Override
    public Status update(BlackBoard board) {
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

        FightUnit enemy = null;

        Circle circle = new Circle(unit.getPosition(), unit.getRadius() + range);
        long unitId = unit.getId();
        List<FightUnit> units = scene.distributed().retrieve(circle).stream().filter(u -> u.getId() != unitId).collect(Collectors.toList());
        if (units.isEmpty()) {
            units = scene.getUnits().stream().filter(u -> u.getId() != unitId).collect(Collectors.toList());

            if (units.isEmpty()) {
                return Status.FAILURE;
            }
            // 选择最近的
            units.sort((o1, o2) -> (int) Vector2D.distance(o1.getPosition(), o2.getPosition()));
            enemy = units.get(0);
            board.setEnemy(enemy);
        } else {
            enemy = units.get(0);
            board.setEnemy(enemy);
        }

        return Status.SUCCESS;
    }
}
