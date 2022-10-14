package com.li.battle.ai.action.move;

import com.li.battle.ai.Status;
import com.li.battle.ai.action.AbstractAction;
import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.core.UnitState;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.util.SteeringBehaviourUtil;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * 移动等待状态
 * @author li-yuanwen
 * @date 2022/10/1
 */
public class MovingWaitingAction extends AbstractAction {

    /** 最大移动等待回合数 **/
    static final int MAX_WAIT_ROUND = 5;

    @Override
    public Status update(BlackBoard board) {

        FightUnit unit = board.getUnit();

        int waitRound = board.incrementWaitRound();
        if (waitRound > MAX_WAIT_ROUND) {
            // 重新寻路
            board.clearWays();
            unit.modifyState(UnitState.MOVING);
        } else {
            // 判断路上有没有障碍
            Vector2D force = SteeringBehaviourUtil.obstacleAvoidance(unit, unit.getScene());
            if (Vector2D.ZERO.equals(force)) {
                unit.modifyState(UnitState.MOVING);
            }
        }

        return Status.SUCCESS;
    }
}
