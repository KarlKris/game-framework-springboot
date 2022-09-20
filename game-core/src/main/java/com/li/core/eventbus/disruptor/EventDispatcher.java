package com.li.core.eventbus.disruptor;

import com.li.core.eventbus.event.IdentityEvent;
import com.li.core.eventbus.event.NamedEvent;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

/**
 * 事件分发器
 * @author li-yuanwen
 * @date 2022/7/18
 */
@Slf4j
public class EventDispatcher implements WorkHandler<DisruptorEvent<?>> {

    private final EventBus eventbus;


    public EventDispatcher(EventBus eventbus) {
        this.eventbus = eventbus;
    }

    @Override
    public void onEvent(DisruptorEvent<?> event) throws Exception {
        dispatch(event);
    }

    public void dispatch(DisruptorEvent<?> event) {
        NamedEvent body = event.getBody();
        String name = body.getName();

        final List<EventHandler<?>> handlers = eventbus.getEventHandlerByName(name);
        if (handlers.isEmpty()) {
            return;
        }
        if (body instanceof IdentityEvent) {
            final IdentityEvent e = (IdentityEvent) body;
            try {
                e.getHandleExecutor().execute(() -> handleEvent(e, handlers));
                return;
            } catch (RejectedExecutionException exception) {
                log.error("无法将事件处理逻辑submit到玩家线程上", exception);
            }
        }

        handleEvent(body, handlers);
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handleEvent(NamedEvent event, List<EventHandler<?>> handlers) {
        for (EventHandler handler : handlers) {
            try {
                handler.onEvent(event);
            } catch (Exception e) {
                log.error("处理事件:[{}]发生未知异常", event, e);
            }
        }
    }

}
