package com.li.core.eventbus.disruptor;


import com.li.core.eventbus.event.NamedEvent;

/**
 * DisruptorEvent 事件处理
 * @author li-yuanwen
 */
public interface EventHandler<B extends NamedEvent> {

    /**
     * 负责的事件
     * @return /
     */
    String getEventType();

    /**
     * 事件处理
     * @param event 事件
     */
    void onEvent(B event);

}
