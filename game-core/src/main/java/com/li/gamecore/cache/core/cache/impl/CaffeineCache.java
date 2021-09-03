package com.li.gamecore.cache.core.cache.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.li.gamecore.cache.core.cache.Cache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 * 基于Caffeine的缓存
 */
@Slf4j
public class CaffeineCache extends AbstractCache {

    /** 缓存 **/
    private final com.github.benmanes.caffeine.cache.Cache<String, Object> cache;


    public CaffeineCache(String cacheName, short maximum, short expire) {
        super(cacheName);
        this.cache = Caffeine.newBuilder()
                .maximumSize(maximum)
                .expireAfterAccess(expire, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void remove(String key) {
        if (log.isDebugEnabled()) {
            log.debug("移除本地缓存[{}]key[{}]", getCacheName(), key);
        }
        this.cache.invalidate(key);
    }

    @Override
    public void put(String key, Object content) {
        if (log.isDebugEnabled()) {
            log.debug("添加本地缓存[{}]key[{}]", getCacheName(), key);
        }
        this.cache.put(key, content);
    }

    @Override
    protected <T> T get0(String key, Class<T> tClass) {
        if (log.isDebugEnabled()) {
            log.debug("尝试从本地缓存[{}]中获取key[{}]", getCacheName(), key);
        }
        return (T) (this.cache.getIfPresent(key));
    }

    @Override
    public void clear() {
        this.cache.invalidateAll();
    }

}
