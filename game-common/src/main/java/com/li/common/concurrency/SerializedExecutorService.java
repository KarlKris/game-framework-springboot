package com.li.common.concurrency;

import java.util.concurrent.Future;

/**
 * 业务线程池接口 支持串行化执行同一请求源的线程池
 * @author li-yuanwen
 * @date 2022/2/8
 */
public interface SerializedExecutorService {


    /**
     * 提交任务
     * @param id 请求源标识
     * @param task 任务
     * @return /
     */
    Future<?> submit(Long id, Runnable task);

    /**
     * 提交任务
     * @param task
     * @return /
     */
    Future<?> submit(Runnable task);

    /**
     * 销毁某个id
     * @param id
     */
    void destroy(long id);

    /**
     * 销毁线程池
     */
    void shutdown();

}
