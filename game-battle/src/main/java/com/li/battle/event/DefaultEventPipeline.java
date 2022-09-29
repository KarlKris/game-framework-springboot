package com.li.battle.event;

import com.li.battle.event.handler.EventHandler;
import com.li.battle.event.core.BattleEventType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * EventPipeline的默认实现
 * @author li-yuanwen
 * @date 2022/5/23
 */
public class DefaultEventPipeline implements EventPipeline {

    /** 事件接收者 **/
    private final EventReceiver receiver;
    /** 责任链头部 **/
    private AbstractEventHandlerContext head;
    /** 责任链尾部 **/
    private AbstractEventHandlerContext tail;

    public DefaultEventPipeline(EventReceiver receiver) {
        this.receiver = receiver;
        this.head = new HeadEventHandlerContext(this);
    }

    @Override
    public EventReceiver eventReceiver() {
        return receiver;
    }

    @Override
    public List<BattleEventType> getEventTypes() {
        if (head == null) {
            return Collections.emptyList();
        }
        List<BattleEventType> types = new LinkedList<>();
        AbstractEventHandlerContext next = head;
        while (next != null) {
            BattleEventType type = next.eventHandler().getEventType();
            if (type != null) {
                types.add(type);
            }
            next = next.next;
        }
        return types;
    }

    @Override
    public EventPipeline addHandler(EventHandler handler) {
        AbstractEventHandlerContext context = newContext(handler);
        if (head == null) {
            head = context;
        } else if (tail == null) {
            head.next = context;
            tail = context;
        } else {
            AbstractEventHandlerContext prev = tail;
            prev.next = context;
            tail = context;
        }
        return this;
    }

    @Override
    public EventHandlerContext firstEventHandlerContext() {
        return head;
    }


    private AbstractEventHandlerContext newContext(EventHandler eventHandler) {
        return new DefaultEventHandlerContext(this, eventHandler);
    }

    /**
     * 事件处理链头部
     */
    private static final class HeadEventHandlerContext extends AbstractEventHandlerContext implements EventHandler {

        public HeadEventHandlerContext(EventPipeline pipeline) {
            super(pipeline);
        }

        @Override
        public BattleEventType getEventType() {
            return null;
        }

        @Override
        public void handle(EventHandlerContext context, Object receiver, Object event) {
            context.fireHandleEvent(event);
        }

        @Override
        public EventHandler eventHandler() {
            return this;
        }
    }
}
