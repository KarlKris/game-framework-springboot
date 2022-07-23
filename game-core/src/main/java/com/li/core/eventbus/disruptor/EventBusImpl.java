package com.li.core.eventbus.disruptor;

import cn.hutool.core.thread.NamedThreadFactory;
import com.li.common.shutdown.ShutdownProcessor;
import com.li.core.eventbus.event.NamedEvent;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * @author li-yuanwen
 */
@Slf4j
@Service
@ConditionalOnBean(EventHandler.class)
public class EventBusImpl implements EventBus, ShutdownProcessor {

    /** disruptor **/
    private Disruptor<DisruptorEvent<?>> disruptor;
    private DisruptorEventProducer disruptorEventProducer;

    @Resource
    private ApplicationContext applicationContext;

    private Map<String, List<EventHandler<? extends NamedEvent>>> handlers;

    @PostConstruct
    public void init() {
        this.handlers = new HashMap<>(4);
        for (EventHandler<?> handler : applicationContext.getBeansOfType(EventHandler.class).values()) {
            List<EventHandler<?>> handlers = this.handlers.computeIfAbsent(handler.getEventType(), k -> new LinkedList<>());
            handlers.add(handler);
        }

        //  开启disruptor
        startDisruptor();
    }

    private void startDisruptor() {
        NamedThreadFactory namedThreadFactory = new NamedThreadFactory("Disruptor事件处理线程池", false);
        // 必须为2的几次方
        int ringBufferSize = 1024 * 8;

        this.disruptor = new Disruptor<>(new DisruptorEventFactory()
                , ringBufferSize, namedThreadFactory, ProducerType.MULTI, new SleepingWaitStrategy());


        // 消费数量
        int num = Math.max(2, Runtime.getRuntime().availableProcessors() >> 1);
        EventDispatcher[] dispatchers = new EventDispatcher[num];
        for (int i = 0; i < num; i++) {
            dispatchers[i] = new EventDispatcher(this);
        }

        // 连接消费端方法,不重复消费消息
        disruptor.handleEventsWithWorkerPool(dispatchers);
        // 全局异常处理
        disruptor.setDefaultExceptionHandler(new DisruptorExceptionHandler());

        // 启动
        RingBuffer<DisruptorEvent<?>> ringBuffer = disruptor.start();
        // 创建生产者
        this.disruptorEventProducer = new DisruptorEventProducer(ringBuffer);

        log.warn("Disruptor队列服务已启动");

    }


    @Override
    public <T extends NamedEvent> void produce(String type, T body) {
        this.disruptorEventProducer.produce(type, body);
    }


    @Override
    public List<EventHandler<?>> getEventHandlerByName(String eventName) {
        return handlers.getOrDefault(eventName, Collections.emptyList());
    }

    @Override
    public int getOrder() {
        return SHUT_DOWN_DISRUPTOR;
    }

    @Override
    public void shutdown() {
        if (this.disruptor == null) {
            return;
        }
        this.disruptor.shutdown();
        log.warn("基于Disruptor的事件驱动异步队列线程池停止");
    }
}
