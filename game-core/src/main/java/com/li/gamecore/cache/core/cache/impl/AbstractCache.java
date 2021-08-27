package com.li.gamecore.cache.core.cache.impl;

import com.li.gamecore.cache.core.cache.Cache;

/**
 * @author li-yuanwen
 * 实现基本的数据统计的缓存基类
 */
public abstract class AbstractCache implements Cache {

    /** 缓存名称 **/
    protected final String cacheName;
    /** 缓存数据统计 **/
    protected final CacheStat cacheStat;

    AbstractCache(String cacheName) {
        this.cacheName = cacheName;
        this.cacheStat = new CacheStat();
    }

    @Override
    public String getCacheName() {
        return this.cacheName;
    }

    @Override
    public CacheStat getCacheStat() {
        return this.cacheStat;
    }

    @Override
    public Object get(Object key) {
        incrementQuery();
        Object value = get0(key);
        if (value != null) {
            incrementHit();
        }
        return value;
    }

    /**
     * 实际获取缓存值,留给子类实现
     * @param key 缓存key
     * @return value
     */
    protected abstract Object get0(Object key);

    protected void incrementQuery() {
        this.cacheStat.incrementQuery();
    }

    protected void incrementHit() {
        this.cacheStat.incrementHit();
    }



}
