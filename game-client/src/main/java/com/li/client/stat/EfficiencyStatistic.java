package com.li.client.stat;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 效率统计器
 * @author li-yuanwen
 * @date 2022/5/7
 */
@Component
public class EfficiencyStatistic {


    private final AtomicLong snGenerator = new AtomicLong(0);


    public long nextSn() {
        return snGenerator.incrementAndGet();
    }


}
