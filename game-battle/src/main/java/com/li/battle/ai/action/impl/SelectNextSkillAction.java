package com.li.battle.ai.action.impl;

import com.li.battle.ai.Status;
import com.li.battle.ai.action.AbstractAction;
import com.li.battle.ai.blackboard.BlackBoard;

/**
 * 选择下一个技能Action
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class SelectNextSkillAction extends AbstractAction  {

    @Override
    public Status update(BlackBoard board) {
        board.incrementSkillIndex();
        return Status.SUCCESS;
    }
}
