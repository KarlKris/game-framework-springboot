package com.li.network.protocol;

import lombok.Getter;
import org.springframework.util.ClassUtils;

/**
 * @author li-yuanwen
 * @date 2021/7/31 14:11
 * 方法调用上下文
 **/
@Getter
public class ProtocolMethodInvokeCtx {

    /** 目标对象 **/
    private final Object target;
    /** 方法上下文 **/
    private final ProtocolMethodCtx protocolMethodCtx;
    /** 是否需要身份标识 **/
    private final boolean identity;

    ProtocolMethodInvokeCtx(Object target, ProtocolMethodCtx protocolMethodCtx) {
        this.target = target;
        this.identity = protocolMethodCtx.identity();
        this.protocolMethodCtx = protocolMethodCtx;

    }

    /**
     * 判断命令逻辑处理的返回结果是否是Void.class
     * @return false Void.class
     */
    public boolean hasResponseClass() {
        Class<?> returnType = protocolMethodCtx.getMethod().getReturnType();
        return ClassUtils.isAssignable(Void.TYPE, returnType);
    }

    /**
     * 协议是否是同步协议
     * @return true 同步协议
     */
    public boolean isSyncMethod() {
        return protocolMethodCtx.isSyncMethod();
    }

}
