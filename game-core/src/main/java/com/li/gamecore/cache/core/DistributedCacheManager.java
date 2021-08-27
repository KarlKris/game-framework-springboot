package com.li.gamecore.cache.core;

import java.util.Set;

/**
 * @author li-yuanwen
 * 分布式缓存管理
 */
public interface DistributedCacheManager {

    /**
     * 根据key获取对象
     *
     * @param key
     * @return
     */
    Object get(final Object key);

    /**
     * 根据正则表达式获取对象
     *
     * @param pattern 正则表达式
     * @return
     */
    Set<Object> getAll(final String pattern);

    /**
     * 设置key-value
     *
     * @param key
     * @param value
     * @param seconds 过期时间(秒)
     */
    void set(final Object key, final Object value, int seconds);

    /**
     * 设置key-value 过期时间使用默认配置值
     *
     * @param key
     * @param value
     */
    void set(final Object key, final Object value);

    /**
     * 根据key判断某一对象是否存在
     *
     * @param key
     * @return 是否存在
     */
    Boolean exists(final Object key);

    /**
     * 根据key删除对象
     *
     * @param key
     */
    void del(final Object key);

    /**
     * 根据正则表达式删除对象
     *
     * @param pattern 正则表达式
     * @return
     */
    void delAll(final String pattern);

    /**
     * 根据key获取对应对象的类型
     *
     * @param key
     * @return 对应对象的类型
     */
    String type(final Object key);

    /**
     * 设置key的过期时间
     *
     * @param key
     * @param seconds
     * @return 是否设置成功
     */
    Boolean expire(final Object key, final int seconds);

    /**
     * 设置key在指定时间点后过期
     *
     * @param key
     * @param unixTime
     * @return 是否成功
     */
    Boolean expireAt(final Object key, final long unixTime);

    /**
     * 获取对应key的过期时间
     *
     * @param key
     * @return
     */
    Long ttl(final Object key);

    /**
     * 设置新值并返回旧值
     *
     * @param key
     * @param value
     * @return 旧值
     */
    Object getAndSet(final Object key, final Object value);


    /**
     * 根据key设置对应哈希表对象的field - value
     *
     * @param key
     * @param field
     * @param value
     */
    void hSet(Object key, Object field, Object value);

    /**
     * 根据key获取对应哈希表的对应field的对象
     *
     * @param key
     * @param field
     * @return
     */
    Object hGet(Object key, Object field);

    /**
     * 根据key删除对应哈希表的对应field的对象
     *
     * @param key
     * @param field
     * @return
     */
    void hDel(Object key, Object field);

    /**
     * 指定的 key 不存在时,为 key 设置指定的value
     *
     * @param key
     * @param value
     * @return 是否设置成功
     */
    boolean setnx(Object key, Object value);

    /**
     * 对应key的值自增
     *
     * @param key
     * @return 自增后的值
     */
    Long incr(Object key);


    /**
     * 将value设置至指定key的set集合中
     *
     * @param key
     * @param value
     */
    void sAdd(Object key, Object value);

    /**
     * 获取指定key的set集合
     *
     * @param key
     * @return
     */
    Set<?> sAll(Object key);

    /**
     * 删除指定key的set集合中的value
     *
     * @param key
     * @param value
     * @return
     */
    boolean sDel(Object key, Object value);

    // ----------- Pub/Sub ------------------

    void publish(String channel, Object msg);

}
