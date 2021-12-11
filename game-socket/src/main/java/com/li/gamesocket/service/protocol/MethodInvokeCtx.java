package com.li.gamesocket.service.protocol;

import lombok.Getter;
import org.springframework.util.ClassUtils;

/**
 * @author li-yuanwen
 * @date 2021/7/31 14:11
 * 方法调用上下文
 **/
@Getter
public class MethodInvokeCtx {

    /** 目标对象 **/
    private final Object target;
    /** 方法上下文 **/
    private final MethodCtx methodCtx;
    /** 是否需要身份标识 **/
    private final boolean identity;

    MethodInvokeCtx(Object target, MethodCtx methodCtx) {
        this.target = target;
        this.identity = methodCtx.identity();
        this.methodCtx = methodCtx;

    }

    /**
     * 判断命令逻辑处理的返回结果是否是Void.class
     * @return false Void.class
     */
    public boolean hasResponseClass() {
        Class<?> returnType = methodCtx.getMethod().getReturnType();
        return ClassUtils.isAssignable(Void.TYPE, returnType);
    }

}
