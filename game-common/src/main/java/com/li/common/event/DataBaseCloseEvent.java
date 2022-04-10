package com.li.common.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * 数据库关闭事件
 * @author li-yuanwen
 */
public class DataBaseCloseEvent extends ApplicationContextEvent {


    public DataBaseCloseEvent(ApplicationContext source) {
        super(source);
    }
}
