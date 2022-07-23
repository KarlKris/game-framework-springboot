package com.li.common.shutdown;

/**
 * 关闭处理器
 * @author li-yuanwen
 * @date 2022/7/19
 */
public interface ShutdownProcessor {

    int SHUT_DOWN_THREAD_POOL = 1;
    int SHUT_DOWN_DISRUPTOR = 2;
    int SHUT_DOWN_DATA_BASE = 3;


    /**
     * 关闭顺序
     * @return 序号(越小越早执行)
     */
    int getOrder();


    /**
     * shut down
     */
    void shutdown();


}
