package com.li.core.redis;

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
    <T> T get(final String key, final Class<T> tClass);

    /**
     * 根据正则表达式获取对象
     *
     * @param pattern 正则表达式
     * @return
     */
    Set<String> getAll(final String pattern);

    /**
     * 设置key-value
     *
     * @param key
     * @param value
     * @param seconds 过期时间(秒)
     */
    void set(final String key, final Object value, int seconds);

    /**
     * 设置key-value 过期时间使用默认配置值
     *
     * @param key
     * @param value
     */
    void set(final String key, final Object value);

    /**
     * 根据key判断某一对象是否存在
     *
     * @param key
     * @return 是否存在
     */
    Boolean exists(final String key);

    /**
     * 根据key删除对象
     *
     * @param key
     */
    void del(final String key);

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
    String type(final String key);

    /**
     * 设置key的过期时间
     *
     * @param key
     * @param seconds
     * @return 是否设置成功
     */
    Boolean expire(final String key, final int seconds);

    /**
     * 设置key在指定时间点后过期
     *
     * @param key
     * @param unixTime
     * @return 是否成功
     */
    Boolean expireAt(final String key, final long unixTime);

    /**
     * 获取对应key的过期时间
     *
     * @param key
     * @return
     */
    Long ttl(final String key);

    /**
     * 设置新值并返回旧值
     *
     * @param key
     * @param value
     * @return 旧值
     */
    <T> T getAndSet(final String key, final T value);

    /**
     * 根据key设置对应哈希表对象的field - value
     *
     * @param key
     * @param field
     * @param value
     */
    void hSet(final String key, final Object field, final Object value);

    /**
     * 根据key获取对应哈希表的对应field的对象
     *
     * @param key
     * @param field
     * @return
     */
    <T> T hGet(final String key, final Object field, final Class<T> tClass);

    /**
     * 根据key删除对应哈希表的对应field的对象
     *
     * @param key
     * @param field
     * @return
     */
    void hDel(final String key, final Object field);

    /**
     * 指定的 key 不存在时,为 key 设置指定的value
     *
     * @param key
     * @param value
     * @return 是否设置成功
     */
    boolean setnx(final String key, final Object value);

    /**
     * 对应key的值自增
     *
     * @param key
     * @return 自增后的值
     */
    Long incr(final String key);


    /**
     * 将value设置至指定key的set集合中
     *
     * @param key
     * @param value
     */
    void sAdd(final String key, final Object value);

    /**
     * 获取指定key的set集合
     *
     * @param key
     * @return
     */
    <T> Set<T> sAll(final String key, final Class<T> tClass);

    /**
     * 删除指定key的set集合中的value
     *
     * @param key
     * @param value
     * @return
     */
    boolean sDel(final String key, final Object value);

    // ----------- Pub/Sub ------------------

    /**
     * 发布消息
     * @param channel the channel to publish to, must not be null
     * @param msg 消息
     */
    void publish(final String channel, final Object msg);

}
