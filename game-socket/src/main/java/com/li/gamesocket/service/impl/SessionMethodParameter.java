package com.li.gamesocket.service.impl;

import com.li.gamesocket.service.MethodParameter;
import com.li.gamesocket.session.Session;

import java.lang.reflect.Type;

/**
 * @author li-yuanwen
 * Session参数
 */
public class SessionMethodParameter implements MethodParameter {

    public static final SessionMethodParameter SESSION_PARAMETER = new SessionMethodParameter();

    @Override
    public String getParameterName() {
        return null;
    }

    @Override
    public Type getParameterType() {
        return Session.class;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public boolean identity() {
        return false;
    }
}
