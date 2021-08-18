package com.li.gamecore.cache.core.processor.impl;

import com.li.gamecore.cache.config.CachedType;
import com.li.gamecore.cache.core.cache.Cache;
import com.li.gamecore.cache.core.cache.impl.CaffeineCache;
import com.li.gamecore.cache.core.processor.CacheProcessor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author li-yuanwen
 * 缓存服务
 */
@Component
public class LocalCacheProcessor implements CacheProcessor {

    /** 实体缓存 **/
    private final ConcurrentHashMap<String, Cache> localCacheHolder = new ConcurrentHashMap<>();

    @Override
    public CachedType getType() {
        return CachedType.LOCAL;
    }

    @Override
    public Cache createCache(String cacheName, short maximum, short expire) {
        CaffeineCache caffeineCache = new CaffeineCache(cacheName, maximum, expire);
        Cache old = this.localCacheHolder.put(cacheName, caffeineCache);
        if (old != null) {
            old.clear();
        }
        return caffeineCache;
    }

    @Override
    public Cache getCache(String cacheName) {
        return this.localCacheHolder.get(cacheName);
    }
}
