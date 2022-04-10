package com.li.core.cache.anno;

import com.li.core.cache.config.CachedType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存移除
 * @author li-yuanwen
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachedEvict {

    /** 缓存类型 **/
    CachedType type() default CachedType.LOCAL;

    /** 缓存名称 支持SpEl表达式 **/
    String name();

    /** key值(支持SpEl表达式) **/
    String key();

}
