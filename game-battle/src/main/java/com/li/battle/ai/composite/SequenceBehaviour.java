package com.li.battle.ai.composite;

import com.li.battle.ai.Status;
import com.li.battle.ai.behaviour.Behaviour;
import com.li.battle.ai.blackboard.BlackBoard;

import java.util.Iterator;

/**
 * 顺序器,依次执行每一个子行为，知道所有节点都成功执行或有一个子节点失败为止。  复合行为  控制节点
 * @author li-yuanwen
 * @date 2022/1/25
 */
public class SequenceBehaviour extends AbstractComposite {

    @Override
    public Status update(BlackBoard board) {
        Iterator<Behaviour> iterator =
                getChildren().iterator();
        // 有子行为
        if (!iterator.hasNext()) {
            return Status.INVALID;
        }

        while (true) {
            Behaviour behaviour = iterator.next();
            Status status = behaviour.tick(board);

            if (status != Status.SUCCESS) {
                return status;
            }

            if (!iterator.hasNext()) {
                return Status.SUCCESS;
            }
        }
    }
}
