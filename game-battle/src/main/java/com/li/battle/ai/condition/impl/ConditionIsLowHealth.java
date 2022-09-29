package com.li.battle.ai.condition.impl;

import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.ai.condition.AbstractCondition;
import com.li.battle.core.Attribute;
import com.li.battle.core.unit.FightUnit;

/**
 * 条件节点----血量低
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class ConditionIsLowHealth extends AbstractCondition {

    @Override
    public boolean valid(BlackBoard board) {
        FightUnit unit = board.getUnit();
        long mpMax = unit.getAttributeValue(Attribute.HP_MAX);
        long curHp = unit.getAttributeValue(Attribute.CUR_HP);
        // 低于10%v
        long lowHp = (long) (mpMax * 0.1D);
        return lowHp >= curHp;
    }
}
