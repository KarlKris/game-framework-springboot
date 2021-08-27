package com.li.gamecore.cache.redis.redisson;

import com.li.gamecore.cache.core.DistributedLockManager;
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
