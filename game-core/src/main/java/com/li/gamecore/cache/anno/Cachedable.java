package com.li.gamecore.cache.anno;

import com.li.gamecore.cache.config.CacheConstants;
import com.li.gamecore.cache.config.CachedType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author li-yuanwen
 * 用于某个方法，希望这个方法添加缓存，此方法被调用的时候，如果有缓存，此方法不执行
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cachedable {

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

    /**
     * null值 是否缓存(缓存穿透)
     * @return true 缓存null值
     */
    boolean nullCache() default false;

    /** 缓存大小 **/
    short maximum() default CacheConstants.DEFAULT_MAXIMUM;

    /** 失效时间(分钟) **/
    short expire() default CacheConstants.DEFAULT_EXPIRE;
}
