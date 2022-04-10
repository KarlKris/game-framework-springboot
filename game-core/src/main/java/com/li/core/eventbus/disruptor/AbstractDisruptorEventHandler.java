package com.li.core.eventbus.disruptor;


import com.li.common.thread.SerializedExecutorService;
import com.li.core.eventbus.event.DisruptorEvent;
import com.li.core.eventbus.event.IdentityEvent;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 */
public abstract class AbstractDisruptorEventHandler<B> implements DisruptorEventHandler<B> {

    @Resource
    private SerializedExecutorService executorService;

    @Override
    public void handleEvent(DisruptorEvent<B> event) {
        final B body = event.getBody();
        if (body instanceof IdentityEvent) {
            // 回到业务线程池执行逻辑,减少并发
            executorService.submit(((IdentityEvent) body).getIdentity()
                    , () -> doHandlerEvent(body));
        } else {
            doHandlerEvent(body);
        }
    }



    /**
     * 实际内容处理
     * @param body 事件内容
     */
    protected abstract void doHandlerEvent(B body);
}
