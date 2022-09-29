package com.li.battle.ai.action.impl;

import com.li.battle.ai.Status;
import com.li.battle.ai.action.AbstractAction;
import com.li.battle.ai.blackboard.BlackBoard;

/**
 * 无任何逻辑的返回Status.SUCCESS 的行为
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class SuccessAction extends AbstractAction {

    @Override
    public Status update(BlackBoard board) {
        return Status.SUCCESS;
    }
}
