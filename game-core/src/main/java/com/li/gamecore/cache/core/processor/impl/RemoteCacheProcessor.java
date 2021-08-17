package com.li.gamecore.cache.core.processor.impl;

import com.li.gamecore.cache.config.CachedType;
import com.li.gamecore.cache.core.Cache;
import com.li.gamecore.cache.core.processor.CacheProcessor;

/**
 * @author li-yuanwen
 * 分布式缓存接口
 */
public class RemoteCacheProcessor implements CacheProcessor {

    @Override
    public CachedType getType() {
        return CachedType.REMOTE;
    }

    @Override
    public Cache createCache(String cacheName, short maximum, short expire) {
        return null;
    }
}
