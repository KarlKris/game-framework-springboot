package com.li.battle.event;

import com.li.battle.event.handler.EventHandler;

/**
 * 负责EventHandler和EventPipeline沟通
 * @author li-yuanwen
 * @date 2022/5/23
 */
public interface EventHandlerContext {

    /**
     * 获取事件接收者实例
     * @return 事件接收者实例
     */
    EventReceiver eventReceiver();

    /**
     * 返回事件接收者的责任链
     * @return 事件接收者的责任链
     */
    EventPipeline eventPipeline();

    /**
     * 事件处理传递
     * @param event 事件内容
     */
    void fireHandleEvent(Object event);

    /**
     * 事件处理器
     * @return 事件处理器
     */
    EventHandler eventHandler();

}
