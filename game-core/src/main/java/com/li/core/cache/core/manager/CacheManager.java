package com.li.core.cache.core.manager;

import com.li.core.cache.config.CachedType;
import com.li.core.cache.core.cache.Cache;

/**
 * @author li-yuanwen
 * 缓存管理
 */
public interface CacheManager {

    /**
     * 创建缓存
     * @param type 缓存类型
     * @param cacheName 缓存名称
     * @param maximum 容量
     * @param expire 时效
     * @return
     */
    Cache createCache(CachedType type, String cacheName, short maximum, short expire);

    /**
     * 查询缓存
     * @param type 缓存类型
     * @param cacheName 缓存名称
     * @return 缓存
     */
    Cache getCache(CachedType type, String cacheName);

}
