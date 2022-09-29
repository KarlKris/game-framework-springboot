package com.li.battle.ai.decorator;

import com.li.battle.ai.Status;
import com.li.battle.ai.blackboard.BlackBoard;

/**
 * 重复执行某行为
 * @author li-yuanwen
 * @date 2022/9/27
 */
public class RepeatDecorator extends AbstractDecorator {

    /** 最大重复次数 **/
    private int limit;
    /** 当前重复次数 **/
    private int count;

    @Override
    public Status update(BlackBoard board) {
        while (true) {
            behaviour.tick(board);
            switch (behaviour.getStatus()) {
                case RUNNING:
                    return Status.SUCCESS;
                case FAILURE:
                    return Status.FAILURE;
                default:
                    break;
            }
            if (++count >= limit) {
                return Status.FAILURE;
            }
        }
    }

    @Override
    public void onInitialize(BlackBoard board) {
        limit = board.getUnit().getSkills().size();
        count = 0;
    }
}
