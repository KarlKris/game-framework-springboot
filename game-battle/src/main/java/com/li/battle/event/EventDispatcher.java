package com.li.battle.event;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.event.core.BattleEvent;
import com.li.battle.event.core.BattleEventType;

import java.util.*;

/**
 * 事件分发器
 * @author li-yuanwen
 * @date 2022/5/18
 */
public class EventDispatcher {

    /** 关联的战斗场景 **/
    private final BattleScene scene;

    /** 注册的所有事件处理器 **/
    private final Map<BattleEventType, List<EventHandlerContext>> contextHolder = new EnumMap<>(BattleEventType.class);
    /** 待处理的事件队列 **/
    private final PriorityQueue<EventElement> queue = new PriorityQueue<>();

    public EventDispatcher(BattleScene scene) {
        this.scene = scene;
    }

    /**
     * 注册事件接收者
     * @param receiver 事件接收者
     */
    public void register(EventReceiver receiver) {
        EventPipeline eventPipeline = receiver.eventPipeline();
        EventHandlerContext context = eventPipeline.firstEventHandlerContext();
        for (BattleEventType type : eventPipeline.getEventTypes()) {
            List<EventHandlerContext> contexts = contextHolder.computeIfAbsent(type, k -> new LinkedList<>());
            contexts.add(context);
        }
    }


    /**
     * 分发事件
     * @param event 事件
     * @param curRound 当前回合数
     * @param delay 延时执行事件处理回合数, 0或-1 立即处理
     */
    public void dispatch(BattleEvent event, long curRound, int delay) {
        if (delay <= 0) {
            handle(event, curRound);
        } else {
            queue.offer(new EventElement(curRound + delay, event));
        }
    }


    /**
     * 每帧的事件处理
     */
    public void update(long curRound) {
        EventElement eventElement = queue.peek();
        while (eventElement != null && eventElement.round <= curRound) {
            queue.poll();
            handle(eventElement.event, curRound);
            eventElement = queue.peek();
        }
    }


    private void handle(BattleEvent event, long curRound) {
        List<EventHandlerContext> contexts = contextHolder.get(event.getType());
        if (contexts == null) {
            return;
        }

        // 处理事件
        Iterator<EventHandlerContext> iterator = contexts.iterator();
        while (iterator.hasNext()) {
            EventHandlerContext context = iterator.next();
            if (context.eventReceiver().isValid(curRound)) {
                // 失效移除
                iterator.remove();
                continue;
            }
            // 处理事件
            context.fireHandleEvent(event);
        }
    }


    private static final class EventElement {

        /** 执行的回合数 **/
        private final long round;
        /** 事件内容 **/
        private final BattleEvent event;

        EventElement(long round, BattleEvent event) {
            this.round = round;
            this.event = event;
        }
    }

}
