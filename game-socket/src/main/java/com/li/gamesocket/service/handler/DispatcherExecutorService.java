package com.li.gamesocket.service.handler;

import com.li.gamesocket.service.session.ISession;

/**
 * 业务分发线程池入口
 * @author li-yuanwen
 * @date 2021/9/2 22:38
 **/
public interface DispatcherExecutorService<S extends ISession> {


    /**
     * 执行方法(根据Session hash一个线程执行)
     * @param runnable
     * @param session
     */
    void execute(S session, Runnable runnable);


    /**
     * 执行方法(随机线程执行)
     * @param runnable
     */
    void execute(Runnable runnable);
}
