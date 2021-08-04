package com.li.gamesocket.session;

import com.li.gamesocket.protocol.Response;
import io.netty.channel.Channel;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;


/**
 * @author li-yuanwen
 */
@Getter
public class SnCtx {

    /** 内部消息序号 **/
    private long innerSn;
    /** future **/
    private CompletableFuture<Response> future;
    /** 请求消息序号 **/
    private Long sn;
    /** 源目标 **/
    private Channel channel;

    /**
     * 判断是否是转发消息而存在的消息序号上下文
     * @return true 转发消息
     */
    public boolean isForward() {
        return this.channel != null;
    }

    SnCtx(long innerSn, long sn, Channel channel) {
        this.sn = sn;
        this.innerSn = innerSn;
        this.channel = channel;
    }

    SnCtx(long innerSn, CompletableFuture<Response> future) {
        this.innerSn = innerSn;
        this.future = future;
    }


}
