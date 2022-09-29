package com.li.battle.ai.condition;

import com.li.battle.ai.Status;
import com.li.battle.ai.behaviour.AbstractBehaviour;
import com.li.battle.ai.blackboard.BlackBoard;
import lombok.extern.slf4j.Slf4j;

/**
 * 条件节点基础类
 * @author li-yuanwen
 * @date 2022/9/27
 */
@Slf4j
public abstract class AbstractCondition extends AbstractBehaviour implements Condition {

    @Override
    public final Status update(BlackBoard board) {
        if (valid(board)) {
            return Status.SUCCESS;
        }
        return Status.FAILURE;
    }
}
