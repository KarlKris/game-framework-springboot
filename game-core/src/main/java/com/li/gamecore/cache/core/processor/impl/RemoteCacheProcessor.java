package com.li.gamecore.cache.core.processor.impl;

import com.li.gamecore.cache.config.CachedType;
import com.li.gamecore.cache.core.cache.Cache;
import com.li.gamecore.cache.core.processor.CacheProcessor;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * 分布式缓存接口
 */
@Component
public class RemoteCacheProcessor implements CacheProcessor {

    @Override
    public CachedType getType() {
        return CachedType.REMOTE;
    }

    @Override
    public Cache createCache(String cacheName, short maximum, short expire) {
        return null;
    }

    @Override
    public Cache getCache(String cacheName) {
        return null;
    }
}
