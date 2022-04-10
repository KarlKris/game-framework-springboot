package com.li.battle.ai.composite;

import com.li.battle.ai.Status;
import com.li.battle.ai.behaviour.Behaviour;

import java.util.Iterator;

/**
 * 选择器，依次执行每一个行为，直到它发现子节点已经成功执行或返回RUNNING状态  控制节点 复合行为
 * @author li-yuanwen
 * @date 2022/1/25
 */
public class SelectorComposite extends AbstractComposite {

    @Override
    public final Status update() {
        Iterator<Behaviour> iterator =
                getChildren().iterator();

        if (!iterator.hasNext()) {
            return Status.INVALID;
        }

        while (true) {
            Behaviour behaviour = iterator.next();
            Status status = behaviour.tick();

            if (status != Status.FAILURE) {
                return status;
            }

            if (!iterator.hasNext()) {
                return Status.FAILURE;
            }
        }
    }
}
