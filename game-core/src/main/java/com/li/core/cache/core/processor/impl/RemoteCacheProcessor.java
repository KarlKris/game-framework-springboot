package com.li.core.cache.core.processor.impl;

import com.li.core.cache.config.CachedType;
import com.li.core.redis.DistributedCacheManager;
import com.li.core.cache.core.cache.Cache;
import com.li.core.cache.core.cache.impl.CaffeineRedisCache;
import com.li.core.cache.core.processor.CacheProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author li-yuanwen
 * 分布式缓存接口
 */
@Component
public class RemoteCacheProcessor implements CacheProcessor {

    @Resource
    private DistributedCacheManager distributedCacheManager;

    /** 实体缓存 **/
    private final ConcurrentHashMap<String, Cache> localCacheHolder = new ConcurrentHashMap<>();

    @Override
    public CachedType getType() {
        return CachedType.REMOTE;
    }

    @Override
    public Cache createCache(String cacheName, short maximum, short expire) {
        CaffeineRedisCache cache = new CaffeineRedisCache(cacheName, maximum, expire);
        cache.setDistributedCacheManager(distributedCacheManager);
        Cache old = this.localCacheHolder.put(cacheName, cache);
        if (old != null) {
            old.clear();
        }
        return cache;
    }

    @Override
    public Cache getCache(String cacheName) {
        return localCacheHolder.get(cacheName);
    }
}
