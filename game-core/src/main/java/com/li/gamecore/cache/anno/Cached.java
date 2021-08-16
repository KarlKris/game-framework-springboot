package com.li.gamecore.cache.anno;

import com.li.gamecore.cache.config.CacheConstants;
import com.li.gamecore.cache.config.CachedType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author li-yuanwen
 * 自定义缓存注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {

    /** 缓存类型 **/
    CachedType type() default CachedType.ENTITY;

    /** 缓存名称 **/
    String name();

    /** 缓存大小 **/
    short maximum() default CacheConstants.DEFAULT_MAXIMUM;

    /** 失效时间 **/
    short expire() default CacheConstants.DEFAULT_EXPIRE;
}
