package com.li.battle.ai.behaviour;

import com.li.battle.ai.Status;

/**
 * 基础节点对象,规定API调用契约
 * @author li-yuanwen
 * @date 2022/1/25
 */
public abstract class AbstractBehaviour implements Behaviour {

    /** 状态 **/
    private Status status;

    @Override
    public final Status tick() {
        // 行为第一次调用
        if (status != Status.RUNNING) {
            onInitialize();
        }
        // 更新状态
        status = update();
        // 行为结束
        if (status != Status.RUNNING) {
            onTerminate(status);
        }

        return status;
    }

    @Override
    public void onInitialize() {
        // 默认空方法
    }

    @Override
    public void onTerminate(Status status) {
        // 默认空方法
    }

    @Override
    public final Status getCurStatus() {
        return status;
    }
}
