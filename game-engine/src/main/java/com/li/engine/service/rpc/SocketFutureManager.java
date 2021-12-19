package com.li.engine.service.rpc;

import com.li.engine.service.rpc.future.SocketFuture;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 远程调用消息Future容器
 * @author li-yuanwen
 * @date 2021/12/10
 */
@Component
public class SocketFutureManager {

    /** 消息序号生成器 **/
    private final AtomicLong snGenerator = new AtomicLong(0);

    /** 消息序号回复Session **/
    private final ConcurrentHashMap<Long, SocketFuture> futureHolder = new ConcurrentHashMap<>();

    /** 获取下一个消息序号 **/
    public long nextSn() {
        return this.snGenerator.incrementAndGet();
    }

    /**
     * 添加远程调用消息Future
     * @param socketFuture 远程调用消息Future
     */
    public void addSocketFuture(SocketFuture socketFuture) {
        this.futureHolder.put(socketFuture.getSn(), socketFuture);
    }

    /**
     * 移除远程调用消息Future
     * @param sn 远程调用消息Future序号
     * @return 远程调用消息Future or null
     */
    public SocketFuture removeSocketFuture(long sn) {
        return this.futureHolder.remove(sn);
    }

}
