package com.li.engine.service.rpc.future;

import com.li.network.message.InnerMessage;

/**
 * 远程调用Future
 * @author li-yuanwen
 * @date 2021/12/10
 */
public abstract class SocketFuture {

    /** 消息序号 **/
    private final long sn;
    /** 执行rpc的线程所在处理的请求消息序号 **/
    private final long outerSn;
    /** 请求方唯一标识 **/
    private final long identity;
    /** 是否是同步调用 **/
    private final boolean sync;

    public SocketFuture(long sn, long outerSn, long identity, boolean sync) {
        this.sn = sn;
        this.outerSn = outerSn;
        this.identity = identity;
        this.sync = sync;
    }

    public long getSn() {
        return sn;
    }

    public long getOuterSn() {
        return outerSn;
    }

    public long getIdentity() {
        return identity;
    }

    public boolean isSync() {
        return sync;
    }

    /**
     * 同步消息结果
     * @param message 消息
     */
    public abstract void complete(InnerMessage message);
}
