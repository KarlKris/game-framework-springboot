package com.li.gamesocket.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对外方法协议注解
 * @author li-yuanwen
 * @date 2021/7/30 21:00
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SocketMethod {

    /** 协议号 **/
    byte id();

}
