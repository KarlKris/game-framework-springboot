package com.li.gamecommon.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * @author li-yuanwen
 * 数据库关闭事件
 */
public class DataBaseCloseEvent extends ApplicationContextEvent {


    public DataBaseCloseEvent(ApplicationContext source) {
        super(source);
    }
}
