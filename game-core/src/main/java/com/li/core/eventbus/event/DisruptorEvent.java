package com.li.core.eventbus.event;

import lombok.Getter;

/**
 * @author li-yuanwen
 * Disruptor 事件载体
 */
@Getter
public class DisruptorEvent<B> {

    /** 事件名称 **/
    private String name;

    /** 事件内容 **/
    private B body;

    /** 事件产生的时间 **/
    private long time;

    public void fillData(String name, B body) {
        this.name = name;
        this.body = body;
        this.time = System.currentTimeMillis();
    }


    public int calculateHandleTime() {
        return (int) (System.currentTimeMillis() - this.time);
    }

}
