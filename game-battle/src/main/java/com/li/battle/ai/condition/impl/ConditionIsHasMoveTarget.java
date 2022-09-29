package com.li.battle.ai.condition.impl;

import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.ai.condition.AbstractCondition;

/**
 * 条件节点----是否有移动需求
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class ConditionIsHasMoveTarget extends AbstractCondition {

    @Override
    public boolean valid(BlackBoard board) {
        return board.getPoint() != null;
    }
}
