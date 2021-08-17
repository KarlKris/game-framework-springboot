package com.li.gamecore.cache.core.processor.impl;

import com.li.gamecore.cache.config.CachedType;
import com.li.gamecore.cache.core.Cache;
import com.li.gamecore.cache.core.processor.CacheProcessor;

/**
 * @author li-yuanwen
 */
public class RegionCacheProcessor implements CacheProcessor {

    @Override
    public CachedType getType() {
        return CachedType.REGION_ENTITY;
    }

    @Override
    public Cache createCache(String cacheName, short maximum, short expire) {
        return null;
    }
}
