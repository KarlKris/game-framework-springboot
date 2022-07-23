package com.li.core.eventbus.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @author li-yuanwen
 */
public class DisruptorEventFactory implements EventFactory<DisruptorEvent<?>> {

    @Override
    public DisruptorEvent<?> newInstance() {
        return new DisruptorEvent<>();
    }

}
