package com.li.gamecore.cache.core;

/**
 * @author li-yuanwen
 * 缓存抽象
 */
public interface Cache {

    /**
     * 查询缓存名称
     * @return 缓存名称
     */
    String getCacheName();


    /**
     * 移除缓存
     * @param key 缓存key
     */
    void remove(String key);

}
