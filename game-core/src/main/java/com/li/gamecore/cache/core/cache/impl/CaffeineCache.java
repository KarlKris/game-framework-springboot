package com.li.gamecore.cache.core.cache.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.li.gamecore.cache.core.cache.Cache;

import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 * 基于Caffeine的缓存
 */
public class CaffeineCache extends AbstractCache {

    /** 缓存 **/
    private final com.github.benmanes.caffeine.cache.Cache<Object, Object> cache;


    public CaffeineCache(String cacheName, short maximum, short expire) {
        super(cacheName);
        this.cache = Caffeine.newBuilder()
                .maximumSize(maximum)
                .expireAfterAccess(expire, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void remove(Object key) {
        this.cache.invalidate(key);
    }

    @Override
    public void put(Object key, Object content) {
        this.cache.put(key, content);
    }

    @Override
    protected Object get0(Object key) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public void clear() {
        this.cache.invalidateAll();
    }

}
