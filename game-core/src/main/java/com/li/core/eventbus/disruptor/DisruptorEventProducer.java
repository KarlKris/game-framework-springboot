package com.li.core.eventbus.disruptor;

import com.li.core.eventbus.event.NamedEvent;
import com.lmax.disruptor.RingBuffer;

/**
 * @author li-yuanwen
 * Disruptor 事件生产者
 */
public class DisruptorEventProducer {

    /** 事件队列 **/
    private RingBuffer<DisruptorEvent<?>> ringBuffer;

    public DisruptorEventProducer(RingBuffer<DisruptorEvent<?>> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public <B extends NamedEvent> void produce(String type, B event) {
        // 1.ringBuffer 事件队列 下一个槽
        long sequence = ringBuffer.next();
        try {
            //2.取出空的事件队列
            DisruptorEvent disruptorEvent = ringBuffer.get(sequence);
            //3.获取事件队列传递的数据
            disruptorEvent.fillData(type, event);
        } finally {
            //4.发布事件
            ringBuffer.publish(sequence);
        }
    }
}
