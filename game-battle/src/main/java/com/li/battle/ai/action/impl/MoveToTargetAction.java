package com.li.battle.ai.action.impl;

import com.li.battle.ai.Status;
import com.li.battle.ai.action.AbstractAction;
import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;

/**
 * 移向目的行为
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class MoveToTargetAction  extends AbstractAction {

    @Override
    public Status update(BlackBoard board) {
        FightUnit unit = board.getUnit();
        BattleScene scene = unit.getScene();
        scene.battleSceneReferee().move(unit.getId(), board.getPoint().getX(), board.getPoint().getY());
        board.setPoint(null);
        return Status.SUCCESS;
    }
}
