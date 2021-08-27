package com.li.gamecore.eventbus.disruptor;


import com.li.gamecore.eventbus.event.DisruptorEvent;

/**
 * @author li-yuanwen
 * DisruptorEvent 事件处理
 */
public interface DisruptorEventHandler<B> {

    /**
     * 负责的事件
     * @return /
     */
    String getHandlerEventType();

    /**
     * 事件处理
     * @param event 事件
     */
    void handleEvent(DisruptorEvent<B> event);

}
