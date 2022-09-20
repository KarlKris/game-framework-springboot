package com.li.core.eventbus.disruptor;

import com.li.core.eventbus.event.NamedEvent;
import lombok.Getter;

/**
 * @author li-yuanwen
 * Disruptor 事件载体
 */
@Getter
public class DisruptorEvent<B extends NamedEvent> {

    /** 事件名称 **/
    private String name;

    /** 事件内容 **/
    private B body;

    public void fillData(String name, B body) {
        this.name = name;
        this.body = body;

    }

}
