package com.li.gamesocket.service.protocol.impl;

import com.li.gamesocket.service.protocol.AbstractMethodParameter;

/**
 * 用@InBody注解装饰的参数
 * @author li-yuanwen
 * @date 2021/7/31 15:13
 **/
public class InBodyMethodParameter extends AbstractMethodParameter {

    public InBodyMethodParameter(Class<?> clazz) {
        super(clazz);
    }


}
