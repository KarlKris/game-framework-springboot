package com.li.gamecore.cache.core.processor.impl;

import com.li.gamecore.cache.config.CachedType;
import com.li.gamecore.cache.core.Cache;
import com.li.gamecore.cache.core.processor.CacheProcessor;

/**
 * @author li-yuanwen
 * 单机缓存服务
 */
public class LocalCacheProcessor implements CacheProcessor {

    @Override
    public CachedType getType() {
        return CachedType.LOCAL;
    }

    @Override
    public Cache createCache(String cacheName, short maximum, short expire) {
        return null;
    }
}
