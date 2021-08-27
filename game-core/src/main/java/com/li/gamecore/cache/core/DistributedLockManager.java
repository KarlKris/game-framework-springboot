package com.li.gamecore.cache.core;

/**
 * @author li-yuanwen
 * 分布式锁管理
 */
public interface DistributedLockManager {

    /**
     * 对key进行加锁
     *
     * @param key key
     * @return 是否加锁成功
     */
    boolean tryLock(String key);

    /**
     * 对key进行解锁
     *
     * @param key key
     */
    void unlock(String key);

}
