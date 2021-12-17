package com.li.engine.service.push;

import com.li.network.protocol.MethodCtx;
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
    private final MethodCtx methodCtx;

    PushMethodCtx(MethodCtx methodCtx) {
        this.methodCtx = methodCtx;
    }

}
