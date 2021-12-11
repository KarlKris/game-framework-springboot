package com.li.gamesocket.service.handler;

/**
 * 线程中执行玩家身份标识ThreadLocal
 * @author li-yuanwen
 * @date 2021/12/11
 */
public class ThreadSessionIdentityHolder {

    private final static ThreadLocal<Long> IDENTITY_THREAD_LOCAL = new ThreadLocal<>();

    public static void setIdentity(long identity) {
        IDENTITY_THREAD_LOCAL.set(identity);
    }

    public static Long getIdentity() {
        return IDENTITY_THREAD_LOCAL.get();
    }

    public static void remove() {
        IDENTITY_THREAD_LOCAL.remove();
    }

}
