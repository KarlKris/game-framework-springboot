﻿package com.li.gamesocket.service.protocol;

/**
 * 方法参数基类
 * @author li-yuanwen
 * @date 2021/12/10
 */
public class AbstractMethodParameter implements MethodParameter {

    /** 参数类型 **/
    private final Class<?> clazz;

    public AbstractMethodParameter(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<?> getParameterClass() {
        return clazz;
    }
}
