package com.li.engine.service.push;

import com.li.network.protocol.ProtocolMethodCtx;
import lombok.Getter;

/**
 * @author li-yuanwen
 * 推送方法上下文
 */
@Getter
public class PushMethodCtx {

    /**
     * 方法上下文
     **/
    private final ProtocolMethodCtx protocolMethodCtx;

    PushMethodCtx(ProtocolMethodCtx protocolMethodCtx) {
        this.protocolMethodCtx = protocolMethodCtx;
    }

}
