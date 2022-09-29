package com.li.battle.ai.behaviour;

import com.li.battle.ai.Status;
import com.li.battle.ai.blackboard.BlackBoard;

/**
 * 抽象行为的概念,行为树的基石。该接口可以被激活，运行和注销.
 * @author li-yuanwen
 * @date 2022/1/25
 */
public interface Behaviour {

    /**
     * API调用契约
     * @param board 黑板
     * @return 当前状态
     */
    Status tick(BlackBoard board);

    /**
     * 初始化
     * @param board 黑板
     */
    void onInitialize(BlackBoard board);

    /**
     * 在每次行为树更新时被调用且仅被调用一次
     * @param board 黑板
     * @return 当前状态
     */
    Status update(BlackBoard board);

    /**
     * 销毁
     * @param board 黑板
     * @param status 状态
     */
    void onTerminate(BlackBoard board, Status status);

    /**
     * 获取节点状态
     * @return 当前状态
     */
    Status getStatus();

}
