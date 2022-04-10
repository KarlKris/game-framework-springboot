package com.li.common.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * 事件驱动异步队列线程关闭事件
 * @author li-yuanwen
 */
public class EventBusCloseEvent extends ApplicationContextEvent {


    public EventBusCloseEvent(ApplicationContext source) {
        super(source);
    }
}
