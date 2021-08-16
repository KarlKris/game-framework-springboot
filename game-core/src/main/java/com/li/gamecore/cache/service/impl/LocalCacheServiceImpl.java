package com.li.gamecore.cache.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.li.gamecore.cache.service.LocalCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author li-yuanwen
 * 本地缓存接口实现
 */
@Component
@Slf4j
public class LocalCacheServiceImpl implements LocalCacheService {

    /** 缓存 **/
    private ConcurrentHashMap<String, Cache> cache;

}
