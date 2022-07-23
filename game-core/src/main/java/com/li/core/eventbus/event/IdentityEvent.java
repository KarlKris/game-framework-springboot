package com.li.core.eventbus.event;

import java.util.concurrent.Executor;

/**
 * 玩家事件
 * @author li-yuanwen
 * @date 2022/3/10
 */
public interface IdentityEvent extends NamedEvent {


    /**
     * 获取玩家标识
     * @return 玩家标识
     */
    Long getIdentity();


    /**
     * 获取玩家的执行器
     * @return 执行器
     */
    Executor getHandleExecutor();

}
