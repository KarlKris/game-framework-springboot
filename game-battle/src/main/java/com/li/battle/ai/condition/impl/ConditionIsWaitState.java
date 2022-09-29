package com.li.battle.ai.condition.impl;

import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.ai.condition.AbstractCondition;
import com.li.battle.core.unit.FightUnit;

/**
 * 条件节点----单位是否处于等待状态
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class ConditionIsWaitState extends AbstractCondition {

    @Override
    public boolean valid(BlackBoard board) {
        FightUnit unit = board.getUnit();
        return unit.isDead() || !unit.getState().canMove();
    }

}
