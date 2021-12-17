package com.li.engine.service.handler;

/**
 * 业务分发线程池入口
 * @author li-yuanwen
 * @date 2021/9/2 22:38
 **/
public interface DispatcherExecutorService {


    /**
     * 执行方法(根据id hash一个线程执行)
     * @param runnable
     * @param id
     */
    void execute(long id, Runnable runnable);


    /**
     * 执行方法(随机线程执行)
     * @param runnable
     */
    void execute(Runnable runnable);
}
