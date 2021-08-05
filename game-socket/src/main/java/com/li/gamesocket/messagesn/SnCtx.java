package com.li.gamesocket.messagesn;

import lombok.Getter;

/**
 * @author li-yuanwen
 * 已发送消息上下文基类
 */
@Getter
public abstract class SnCtx {

    /** 内部消息序号 **/
    private long innerSn;

    protected SnCtx(long innerSn) {
        this.innerSn = innerSn;
    }

}
