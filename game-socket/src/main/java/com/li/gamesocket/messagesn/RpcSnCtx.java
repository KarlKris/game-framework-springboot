package com.li.gamesocket.messagesn;

import com.li.gamesocket.protocol.Response;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;

/**
 * @author li-yuanwen
 * 用于调用远方接口的上下文
 */
@Getter
public class RpcSnCtx extends SnCtx {

    /** future **/
    private CompletableFuture<Response> future;

    protected RpcSnCtx(long innerSn, CompletableFuture<Response> future) {
        super(innerSn);
        this.future = future;
    }

}
