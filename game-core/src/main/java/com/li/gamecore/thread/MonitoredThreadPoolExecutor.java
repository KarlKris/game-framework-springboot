package com.li.gamecore.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 * @date 2021/8/3 22:54
 * 包含监控数据的线程池
 **/
@Slf4j
public class MonitoredThreadPoolExecutor extends ThreadPoolExecutor {



    public MonitoredThreadPoolExecutor(int corePoolSize, int maximumPoolSize
            , long keepAliveTime, TimeUnit unit
            , BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }
}
