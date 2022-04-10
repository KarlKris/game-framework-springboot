package com.li.core.redis.redisson;

import com.li.core.redis.DistributedLockManager;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 */
@Component
public class RedissonHelper implements DistributedLockManager {

    private final RedissonClient redissonClient;

    @Autowired
    public RedissonHelper(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;

    }

    private RLock getLock(String key) {
        return redissonClient.getLock(key);
    }

    @Override
    public void lock(String key) {
        RLock rLock = getLock(key);
        rLock.lock();
    }

    @Override
    public boolean tryLock(String key) {
        RLock rLock = getLock(key);
        return rLock.tryLock();
    }

    @Override
    public void unlock(String key) {
        RLock rLock = getLock(key);
        rLock.unlock();
    }
}
