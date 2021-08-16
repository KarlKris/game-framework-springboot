package com.li.gamecore.cache.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author li-yuanwen
 * 缓存移除注解(基于非实体缓存)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachedRemove {

    /** 缓存名称 **/
    String name();

    /** id值(支持SpEl表达式) **/
    String id();

}
