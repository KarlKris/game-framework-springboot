package com.li.gamesocket.service.impl;

import com.li.gamesocket.service.MethodParameter;

import java.lang.reflect.Type;

/**
 * @author li-yuanwen
 * @date 2021/7/31 15:11
 * 身份标识参数,用@Identity注解装饰的参数
 **/
public class IdentityMethodParameter implements MethodParameter {

    public static final IdentityMethodParameter IDENTITY_PARAMETER = new IdentityMethodParameter();


    @Override
    public String getParameterName() {
        return null;
    }

    @Override
    public Type getParameterType() {
        return Long.TYPE;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public boolean identity() {
        return true;
    }
}
