package com.li.gamecore.cache.core.manager.impl;

import com.li.gamecore.cache.config.CachedType;
import com.li.gamecore.cache.core.cache.Cache;
import com.li.gamecore.cache.core.manager.CacheManager;
import com.li.gamecore.cache.core.processor.CacheProcessor;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author li-yuanwen
 */
@Component
public class CacheManagerImpl implements CacheManager {

    @Autowired
    private ApplicationContext applicationContext;

    private Map<CachedType, CacheProcessor> processorHolder;

    @PostConstruct
    private void init() {
        processorHolder = new HashMap<>(CachedType.values().length);
        for (CacheProcessor processor : this.applicationContext.getBeansOfType(CacheProcessor.class).values()) {
            if (processorHolder.putIfAbsent(processor.getType(), processor) != null) {
                throw new BeanInitializationException("存在相同的["+ processor.getType().name() +"]CacheProcessor");
            }
        }

        if (processorHolder.size() != CachedType.values().length) {
            throw new BeanInitializationException("CacheProcessor数量["+ processorHolder.size()
                    +"]与缓存类型数量[" + CachedType.values().length + "]不相等");
        }

    }

    @Override
    public Cache createCache(CachedType type, String cacheName, short maximum, short expire) {
        return processorHolder.get(type).createCache(cacheName, maximum, expire);
    }

    @Override
    public Cache getCache(CachedType type, String cacheName) {
        return processorHolder.get(type).getCache(cacheName);
    }
}
