package com.li.battle.event;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.event.core.BattleEvent;
import com.li.battle.event.core.BattleEventType;
import org.springframework.util.CollectionUtils;

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
    private final PriorityQueue<EventElement> queue = new PriorityQueue<>(Comparator.comparingLong(o -> o.round));
    /** EventReceiver持有者汇总 **/
    private final Map<Long, List<EventReceiver>> receivers = new HashMap<>();

    public EventDispatcher(BattleScene scene) {
        this.scene = scene;
    }

    /**
     * 注册事件链
     * @param eventPipeline 事件链
     */
    public void register(EventPipeline eventPipeline) {
        EventReceiver receiver = eventPipeline.eventReceiver();
        EventHandlerContext context = eventPipeline.firstEventHandlerContext();
        for (BattleEventType type : eventPipeline.getEventTypes()) {
            List<EventHandlerContext> contexts = contextHolder.computeIfAbsent(type, k -> new LinkedList<>());
            contexts.add(context);
        }

        List<EventReceiver> eventReceivers = receivers.computeIfAbsent(receiver.getOwner(), k -> new LinkedList<>());
        eventReceivers.add(receiver);
    }


    /**
     * 注销事件接收者
     * @param owner 事件接收者持有者标识,即单位唯一标识
     */
    public void unregister(long owner) {
        List<EventReceiver> eventReceivers = receivers.remove(owner);
        if (!CollectionUtils.isEmpty(eventReceivers)) {
            eventReceivers.forEach(EventReceiver::expire);
        }
    }

    /**
     * 分发事件
     * @param event 事件
     */
    public void dispatch(BattleEvent event) {
        dispatch(event, 0);
    }


    /**
     * 分发事件
     * @param event 事件
     * @param delay 延时执行事件处理回合数, 0或-1 立即处理
     */
    public void dispatch(BattleEvent event, int delay) {
        if (delay <= 0) {
            handle(event);
        } else {
            queue.offer(new EventElement(scene.getSceneRound() + delay, event));
        }
    }


    /**
     * 每帧的事件处理
     */
    public void update() {
        EventElement eventElement = queue.peek();
        while (eventElement != null && eventElement.round <= scene.getSceneRound()) {
            queue.poll();
            handle(eventElement.event);
            eventElement = queue.peek();
        }
    }


    private void handle(BattleEvent event) {
        List<EventHandlerContext> contexts = contextHolder.get(event.getType());
        if (contexts == null) {
            return;
        }

        // 处理事件
        Iterator<EventHandlerContext> iterator = contexts.iterator();
        while (iterator.hasNext()) {
            EventHandlerContext context = iterator.next();
            if (context.eventReceiver().isExpire()) {
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
