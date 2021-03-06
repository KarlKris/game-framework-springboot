package com.li.gamecore.cache.anno;

import com.li.gamecore.cache.config.CacheConstants;
import com.li.gamecore.cache.config.CachedType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author li-yuanwen
 * 用于某个方法，每次被调用，此方法都执行，并把结果更新到PutCache配置的地方，一般用于缓存更新
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachedPut {

    /** 缓存类型 **/
    CachedType type() default CachedType.LOCAL;

    /**
     * 缓存名称 支持SpEl表达式
     * @return 缓存名称
     */
    String name();

    /**
     * 缓存内容标识
     * @return 缓存内容标识
     */
    String key();

    /** 缓存大小 **/
    short maximum() default CacheConstants.DEFAULT_MAXIMUM;

    /** 失效时间(分钟)  **/
    short expire() default CacheConstants.DEFAULT_EXPIRE;

}
