package com.li.battle.ai.condition.impl;

import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.ai.condition.AbstractCondition;
import com.li.battle.core.UnitState;

/**
 * 条件节点----单位是否正处于移动阶段
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class ConditionIsMoveState extends AbstractCondition {

    @Override
    public boolean valid(BlackBoard board) {
        return board.getUnit().getState() == UnitState.MOVING;
    }

}
