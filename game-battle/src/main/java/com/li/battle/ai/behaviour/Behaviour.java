package com.li.battle.ai.behaviour;

import com.li.battle.ai.Status;

/**
 * 抽象行为的概念,行为树的基石。该接口可以被激活，运行和注销.
 * @author li-yuanwen
 * @date 2022/1/25
 */
public interface Behaviour {

    /**
     * API调用契约
     * @return 当前状态
     */
    Status tick();

    /**
     * 初始化
     */
    void onInitialize();

    /**
     * 在每次行为树更新时被调用且仅被调用一次
     * @return 当前状态
     */
    Status update();

    /**
     * 销毁
     */
    void onTerminate(Status status);

    /**
     * 获取节点状态
     * @return 当前状态
     */
    Status getCurStatus();

}
