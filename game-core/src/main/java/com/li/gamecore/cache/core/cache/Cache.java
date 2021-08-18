package com.li.gamecore.cache.core.cache;

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
    void remove(Object key);


    /**
     * 更新缓存
     * @param key 缓存key
     * @param content 缓存内容
     */
    void put(Object key, Object content);

    /**
     * 查询缓存
     * @param key 缓存key
     * @return 缓存内容
     */
    Object get(Object key);

    /**
     * 清空缓存
     */
    void clear();

}
