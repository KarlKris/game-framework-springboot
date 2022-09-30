package com.li.battle.ai.action.impl;

import com.li.battle.ai.Status;
import com.li.battle.ai.action.AbstractAction;
import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.core.Skill;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.selector.SelectParam;

/**
 * 使用技能Action
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class UseSkillAction extends AbstractAction  {

    @Override
    public Status update(BlackBoard board) {
        FightUnit unit = board.getUnit();
        BattleScene battleScene = unit.getScene();

        Skill skill = unit.getSkills().get(board.getSkillIndex());
        SelectParam selectParam = new SelectParam();
        selectParam.setTarget(board.getEnemy().getId());
        battleScene.battleSceneReferee().useSkill(unit.getId(), skill.getSkillId(), selectParam);

        board.setEnemy(null);

        return Status.SUCCESS;
    }
}
