package com.li.gamecore.cache.core.cache.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.li.gamecommon.ApplicationContextHolder;
import com.li.gamecore.cache.core.DistributedCacheManager;
import com.li.gamecore.cache.redis.pubsub.CacheOfPubSubMessage;
import com.li.gamecore.cache.redis.pubsub.PubSubConstants;

import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 * 基于Caffeine+Redis的两级缓存(无锁竞争)
 */
public class CaffeineRedisCache extends AbstractCache {

    /** 一级缓存 **/
    private final com.github.benmanes.caffeine.cache.Cache<Object, Object> cache;
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
    protected Object get0(Object key) {
        // 先从一级缓存中获取
        Object value = this.cache.getIfPresent(key);
        if (value == null) {
            // 尝试从Redis中获取
            value = this.distributedCacheManager.get(key);
            // 添加至一级缓存
            if (value != null) {
                this.cache.put(key, value);
            }
        }
        return value;
    }

    @Override
    public void remove(Object key) {
        // 先移除二级缓存
        this.distributedCacheManager.del(key);
        // 在移除一级缓存
        this.cache.invalidate(key);
        notifyOtherToRemove(key);
    }

    @Override
    public void put(Object key, Object content) {
        // 先添加一级缓存
        this.cache.put(key, content);
        // 再添加二级缓存
        this.distributedCacheManager.set(key, content, expire);
    }

    @Override
    public void clear() {
        this.cache.invalidateAll();
    }

    /** 通知其他进程移除指定key缓存 **/
    private void notifyOtherToRemove(Object key) {
        this.distributedCacheManager.publish(PubSubConstants.CACHE_CHANNEL
                , new CacheOfPubSubMessage(getCacheName(), key));
    }
}
