package com.li.network.protocol;

import com.li.network.session.ISession;

/**
 * 用@Session注解装饰的参数
 * @author li-yuanwen
 */
public class SessionMethodParameter extends AbstractMethodParameter {

    public SessionMethodParameter() {
        super(ISession.class);
    }

    public static final SessionMethodParameter SESSION_PARAMETER = new SessionMethodParameter();

}
