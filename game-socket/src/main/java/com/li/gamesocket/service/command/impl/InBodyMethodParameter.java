package com.li.gamesocket.service.command.impl;

import com.li.gamesocket.service.command.MethodParameter;

import java.lang.reflect.Type;

/**
 * @author li-yuanwen
 * @date 2021/7/31 15:13
 * 用@InBody注解装饰的参数
 **/
public class InBodyMethodParameter implements MethodParameter {

    /** 参数名 **/
    private String name;
    /** 参数类型 **/
    private Type type;
    /** 是否不允许为null **/
    private boolean required;

    public InBodyMethodParameter(String name, Type type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }

    public String getParameterName() {
        return name;
    }

    public Type getParameterType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

}