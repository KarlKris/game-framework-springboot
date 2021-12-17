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

    SocketFuture(long sn) {
        this.sn = sn;
    }

    public long getSn() {
        return sn;
    }


    /**
     * 同步消息结果
     * @param message 消息
     */
    public abstract void complete(InnerMessage message);
}
