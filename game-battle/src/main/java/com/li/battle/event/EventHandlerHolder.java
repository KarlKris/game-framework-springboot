package com.li.battle.event;

import com.li.battle.event.handler.EventHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * EventHandler持有类
 * @author li-yuanwen
 * @date 2022/5/26
 */
@Component
public class EventHandlerHolder {


    @Resource
    private ApplicationContext applicationContext;


    private Map<Class<?>, EventHandler> eventHandlerHolder;


    @PostConstruct
    private void initialize() {
        Collection<EventHandler> eventHandlers = applicationContext.getBeansOfType(EventHandler.class).values();
        eventHandlerHolder = new HashMap<>(eventHandlers.size());
        for (EventHandler eventHandler : eventHandlers) {
            eventHandlerHolder.put(eventHandler.getClass(), eventHandler);
        }
    }


    public EventHandler getEventHandler(Class<?> clz) {
        return eventHandlerHolder.get(clz);
    }


}
