package com.li.core.eventbus.disruptor;


import com.li.core.eventbus.event.NamedEvent;

import java.util.List;

/**
 * @author li-yuanwen
 */
public interface EventBus {

    /**
     * 生产事件
     * @param type 事件类型
     * @param body 事件内容
     * @param <T> 事件类
     */
    <T extends NamedEvent> void produce(String type, T body);


    /**
     * 根据事件名称获取相应的处理器
     * @param eventName 事件名称
     * @return 处理器集
     */
    List<EventHandler<?>> getEventHandlerByName(String eventName);

}
