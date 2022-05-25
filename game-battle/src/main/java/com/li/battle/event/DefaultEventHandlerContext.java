package com.li.battle.event;

import com.li.battle.event.handler.EventHandler;

/**
 * EventHandlerContext的默认实现
 * @author li-yuanwen
 * @date 2022/5/23
 */
public class DefaultEventHandlerContext extends AbstractEventHandlerContext {

    /** 事件处理器 **/
    private final EventHandler eventHandler;

    DefaultEventHandlerContext(EventPipeline pipeline, EventHandler eventHandler) {
        super(pipeline);
        this.eventHandler = eventHandler;
    }

    @Override
    public EventHandler eventHandler() {
        return eventHandler;
    }
}
