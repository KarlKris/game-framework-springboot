package com.li.gamecore.cache.anno;

import com.li.gamecore.cache.config.CachedType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author li-yuanwen
 * 调用有此注解的方法可以删除一个缓存
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachedRemove {

    /** 缓存类型 **/
    CachedType type() default CachedType.ENTITY;

    /** 缓存名称 支持SpEl表达式 **/
    String name();

    /** key值(支持SpEl表达式) **/
    String key();

}
