package com.li.gamesocket.service.command.impl;

import com.li.gamesocket.service.command.MethodParameter;
import com.li.gamesocket.service.session.Session;

import java.lang.reflect.Type;

/**
 * @author li-yuanwen
 * Session参数
 */
public class SessionMethodParameter implements MethodParameter {

    public static final String TYPE = "Session";

    @Override
    public String type() {
        return TYPE;
    }

    public static final SessionMethodParameter SESSION_PARAMETER = new SessionMethodParameter();

    public Type getParameterType() {
        return Session.class;
    }

}
