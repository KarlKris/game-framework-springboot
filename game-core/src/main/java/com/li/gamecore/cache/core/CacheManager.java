package com.li.gamecore.cache.core;

import com.li.gamecore.cache.config.CachedType;

/**
 * @author li-yuanwen
 * 缓存管理
 */
public interface CacheManager {

    /**
     * 创建缓存
     * @param type
     * @param name
     * @param maximum
     * @param expire
     * @return
     */
    Cache createCache(CachedType type, String name, short maximum, short expire);

    /**
     * 查询缓存
     * @param type 缓存类型
     * @param name 缓存名称
     * @return 缓存
     */
    Cache getCache(CachedType type, String name);


}
