package com.li.gamesocket.service;

import lombok.Getter;

import java.lang.reflect.Method;

/**
 * @author li-yuanwen
 * @date 2021/7/31 14:11
 * 命令调用上下文
 **/
@Getter
public class MethodInvokeCtx {

    /** 目标对象 **/
    private Object target;
    /** 具体方法 **/
    private Method method;
    /** 方法参数 **/
    private MethodParameter[] params;
    /** 是否需要身份标识 **/
    private boolean identity;

    MethodInvokeCtx(Object target, Method method, MethodParameter[] params) {
        for (MethodParameter parameter : params) {
            if (parameter.identity()) {
                this.identity = true;
                break;
            }
        }

        this.target = target;
        this.method = method;
        this.params = params;
    }
}
