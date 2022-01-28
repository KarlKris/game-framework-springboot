package com.li.behaviortree.composite;

import com.li.behaviortree.Status;
import com.li.behaviortree.behaviour.Behaviour;

import java.util.Iterator;

/**
 * 顺序器,依次执行每一个子行为，知道所有节点都成功执行或有一个子节点失败为止。  复合行为  控制节点
 * @author li-yuanwen
 * @date 2022/1/25
 */
public class SequenceComposite extends AbstractComposite {

    @Override
    public Status update() {
        Iterator<Behaviour> iterator =
                getChildren().iterator();
        // 有子行为
        if (!iterator.hasNext()) {
            return Status.INVALID;
        }

        while (true) {
            Behaviour behaviour = iterator.next();
            Status status = behaviour.tick();

            if (status != Status.SUCCESS) {
                return status;
            }

            if (!iterator.hasNext()) {
                return Status.SUCCESS;
            }
        }
    }
}
