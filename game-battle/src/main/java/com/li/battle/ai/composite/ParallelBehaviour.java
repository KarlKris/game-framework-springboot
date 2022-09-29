package com.li.battle.ai.composite;

import com.li.battle.ai.Status;
import com.li.battle.ai.behaviour.Behaviour;
import com.li.battle.ai.blackboard.BlackBoard;

import java.util.Iterator;

/**
 * 并行器，根据并行策略执行子行为，复合行为，控制节点
 * @author li-yuanwen
 * @date 2022/1/25
 */
public class ParallelBehaviour extends AbstractComposite {

    /** 成功并行策略 **/
    private final ParallelPolicy successPolicy;
    /**失败并行策略  **/
    private final ParallelPolicy failurePolicy;

    public ParallelBehaviour(ParallelPolicy successPolicy, ParallelPolicy failurePolicy) {
        this.successPolicy = successPolicy;
        this.failurePolicy = failurePolicy;
    }

    @Override
    public final Status update(BlackBoard board) {
        int successCount = 0;
        int failureCount = 0;

        int size = getChildren().size();

        Iterator<Behaviour> iterator =
                getChildren().iterator();

        if (!iterator.hasNext()) {
            return Status.INVALID;
        }

        while (iterator.hasNext()) {
            Behaviour behaviour = iterator.next();
            Status status = behaviour.tick(board);

            if (status == Status.SUCCESS) {
                successCount++;

                if (successPolicy == ParallelPolicy.REQUIRE_ONE) {
                    return status;
                }
            }

            if (status == Status.FAILURE ) {
                failureCount++;

                if (failurePolicy == ParallelPolicy.REQUIRE_ONE) {
                    return status;
                }

            }
        }

        if (failurePolicy == ParallelPolicy.REQUIRE_ALL && failureCount == size) {
            return Status.FAILURE;
        }

        if (successPolicy == ParallelPolicy.REQUIRE_ALL && successCount == size) {
            return Status.SUCCESS;
        }

        return Status.RUNNING;
    }
}
