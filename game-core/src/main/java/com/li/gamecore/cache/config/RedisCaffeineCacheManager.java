package com.li.gamecore.cache.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author li-yuanwen
 */
@Component
@Slf4j
public class RedisCaffeineCacheManager implements CacheManager {




    @Override
    public Cache getCache(String cacheName) {
        return null;
    }

    @Override
    public Collection<String> getCacheNames() {
        return null;
    }
}
