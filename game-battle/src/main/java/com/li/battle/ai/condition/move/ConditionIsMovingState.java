package com.li.battle.ai.condition.move;

import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.ai.condition.AbstractCondition;
import com.li.battle.core.UnitState;

/**
 * 单位是否正处于移动中状态
 * @author li-yuanwen
 * @date 2022/9/30
 */
public class ConditionIsMovingState extends AbstractCondition {

    @Override
    public boolean valid(BlackBoard board) {
        return board.getUnit().getState() == UnitState.MOVING;
    }
}
