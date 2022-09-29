package com.li.battle.ai.behaviour;

import com.li.battle.ai.Status;
import com.li.battle.ai.blackboard.BlackBoard;

/**
 * 基础节点对象,规定API调用契约
 * @author li-yuanwen
 * @date 2022/1/25
 */
public abstract class AbstractBehaviour implements Behaviour {

    /** 状态 **/
    private Status status;

    @Override
    public final Status tick(BlackBoard board) {
        // 行为第一次调用
        if (status != Status.RUNNING) {
            onInitialize(board);
        }
        // 更新状态
        status = update(board);
        // 行为结束
        if (status != Status.RUNNING) {
            onTerminate(board, status);
        }

        return status;
    }

    @Override
    public void onInitialize(BlackBoard board) {
        // 默认空方法
    }

    @Override
    public void onTerminate(BlackBoard board, Status status) {
        // 默认空方法
    }

    @Override
    public final Status getStatus() {
        return status;
    }
}
