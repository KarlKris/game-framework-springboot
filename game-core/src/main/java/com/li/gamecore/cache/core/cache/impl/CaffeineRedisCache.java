package com.li.gamecore.cache.core.cache.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.li.gamecommon.ApplicationContextHolder;
import com.li.gamecore.cache.core.DistributedCacheManager;
import com.li.gamecore.cache.redis.pubsub.CacheOfPubSubMessage;
import com.li.gamecore.cache.redis.pubsub.PubSubConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 * 基于Caffeine+Redis的两级缓存(无锁竞争)
 */
@Slf4j
public class CaffeineRedisCache extends AbstractCache {

    /** 一级缓存 **/
    private final com.github.benmanes.caffeine.cache.Cache<String, Object> cache;
    /** 分布式时效时间(秒) **/
    private final int expire;
    /** 二级缓存 **/
    private final DistributedCacheManager distributedCacheManager;


    public CaffeineRedisCache(String cacheName, short maximum, short expire) {
        super(cacheName);
        // 二级缓存时效 5倍于一级缓存(一级缓存时效应短些)
        this.expire = expire * 60 * 5;
        // 一级缓存
        this.cache = Caffeine.newBuilder()
                .maximumSize(maximum)
                .expireAfterAccess(expire, TimeUnit.MINUTES)
                .build();

        // 二级缓存
        this.distributedCacheManager = ApplicationContextHolder.getBean(DistributedCacheManager.class);
    }

    @Override
    protected <T> T get0(String key, Class<T> tClass) {
        if (log.isDebugEnabled()) {
            log.debug("尝试从本地缓存[{}]中获取key[{}]", getCacheName(), key);
        }
        // 先从一级缓存中获取
        Object value = this.cache.getIfPresent(key);
        if (value == null) {

            if (log.isDebugEnabled()) {
                log.debug("尝试从Redis中获取key[{}]", toRedisKey(key));
            }
            // 尝试从Redis中获取
            value = this.distributedCacheManager.get(toRedisKey(key));
            // 添加至一级缓存
            if (value != null) {
                ObjectMapper objectMapper = ApplicationContextHolder.getBean(ObjectMapper.class);
                T t = objectMapper.convertValue(value, tClass);
                this.cache.put(key, t);
                return t;
            }
            return null;
        }else {
            return (T) value;
        }
    }

    @Override
    public void remove(String key) {
        // 先移除二级缓存
        this.distributedCacheManager.del(toRedisKey(key));
        // 在移除一级缓存
        removeLocalKey(key);
        notifyOtherToRemove(key);
    }

    @Override
    public void put(String key, Object content) {
        if (log.isDebugEnabled()) {
            log.debug("添加缓存[{}]key[{}]", getCacheName(), key);
        }
        // 先添加一级缓存
        this.cache.put(key, content);
        // 再添加二级缓存
        this.distributedCacheManager.set(toRedisKey(key), content, expire);
    }

    @Override
    public void clear() {
        this.cache.invalidateAll();
    }

    /** 通知其他进程移除指定key缓存 **/
    private void notifyOtherToRemove(String key) {
        this.distributedCacheManager.publish(PubSubConstants.CACHE_CHANNEL
                , new CacheOfPubSubMessage(getCacheName(), key));
    }

    /** 移除本地缓存 **/
    public void removeLocalKey(String key) {
        if (log.isDebugEnabled()) {
            log.debug("移除本地缓存[{}]key[{}]", getCacheName(), key);
        }
        this.cache.invalidate(key);
    }

    /** 分隔符 **/
    static final String SPLIT = ":";

    /** 构建redis可以 **/
    private String toRedisKey(String key) {
        return getCacheName() + SPLIT + key;
    }
}
