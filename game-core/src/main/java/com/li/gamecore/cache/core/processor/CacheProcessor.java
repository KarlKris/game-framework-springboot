package com.li.gamecore.cache.core.processor;


import com.li.gamecore.cache.config.CachedType;
import com.li.gamecore.cache.core.Cache;

/**
 * @author li-yuanwen
 * 缓存接口
 */
public interface CacheProcessor {


    /**
     * 查询缓存类型
     * @return 缓存类型
     */
    CachedType getType();


    /**
     * 创建缓存
     * @param cacheName 缓存名称
     * @param maximum 容量
     * @param expire 时效
     * @return /
     */
    Cache createCache(String cacheName, short maximum, short expire);
}
