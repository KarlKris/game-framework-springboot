package com.li.gamecommon.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * @author li-yuanwen
 * 事件驱动异步队列线程关闭事件
 */
public class EventBusCloseEvent extends ApplicationContextEvent {


    public EventBusCloseEvent(ApplicationContext source) {
        super(source);
    }
}
