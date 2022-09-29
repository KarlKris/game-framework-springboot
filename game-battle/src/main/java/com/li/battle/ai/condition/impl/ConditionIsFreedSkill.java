package com.li.battle.ai.condition.impl;

import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.ai.condition.AbstractCondition;

/**
 * 条件节点----单位能否处于可以释放技能的状态
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class ConditionIsFreedSkill extends AbstractCondition {

    @Override
    public boolean valid(BlackBoard board) {
        return board.getUnit().getState().canFreed();
    }
}
