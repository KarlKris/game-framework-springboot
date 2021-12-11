package com.li.gamesocket.service.protocol.impl;

import com.li.gamesocket.service.protocol.AbstractMethodParameter;

import java.util.Collection;

/**
 * 用@PushIds注解装饰的参数
 * @author li-yuanwen
 */
public class PushIdsMethodParameter extends AbstractMethodParameter {

    public static final PushIdsMethodParameter PUSH_IDS_PARAMETER = new PushIdsMethodParameter();

    public PushIdsMethodParameter() {
        super(Collection.class);
    }
}
