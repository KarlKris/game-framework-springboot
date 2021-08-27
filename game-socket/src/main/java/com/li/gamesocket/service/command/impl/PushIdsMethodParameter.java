package com.li.gamesocket.service.command.impl;

import com.li.gamesocket.service.command.MethodParameter;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * @author li-yuanwen
 */
public class PushIdsMethodParameter implements MethodParameter {

    public static final PushIdsMethodParameter PUSH_IDS_METHOD_PARAMETER = new PushIdsMethodParameter();

    public Type getParameterType() {
        return Collection.class;
    }
}
