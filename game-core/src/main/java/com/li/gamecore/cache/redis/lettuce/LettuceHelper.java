package com.li.gamecore.cache.redis.lettuce;

import com.li.gamecore.cache.core.DistributedCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 * 基于Luttuce的工具类
 */
@Component
public class LettuceHelper implements DistributedCacheManager {


    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public Object get(Object key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Set<Object> getAll(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public void set(Object key, Object value, int seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    @Override
    public void set(Object key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Boolean exists(Object key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void del(Object key) {
        redisTemplate.delete(key);
    }

    @Override
    public void delAll(String pattern) {
        Set<Object> keys = redisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        redisTemplate.delete(keys);
    }

    @Override
    public String type(Object key) {
        DataType type = redisTemplate.type(key);
        return type == null ? null : type.name();
    }

    @Override
    public Boolean expire(Object key, int seconds) {
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    @Override
    public Boolean expireAt(Object key, long unixTime) {
        return redisTemplate.expireAt(key, new Date(unixTime));
    }

    @Override
    public Long ttl(Object key) {
        return redisTemplate.getExpire(key);
    }

    @Override
    public Object getAndSet(Object key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    @Override
    public void hSet(Object key, Object field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public Object hGet(Object key, Object field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    @Override
    public void hDel(Object key, Object field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    @Override
    public boolean setnx(Object key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    @Override
    public Long incr(Object key) {
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public void sAdd(Object key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    @Override
    public Set<?> sAll(Object key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public boolean sDel(Object key, Object value) {
        return redisTemplate.opsForSet().remove(key, value) == 1;
    }

    @Override
    public void publish(String channel, Object msg) {
        redisTemplate.convertAndSend(channel, msg);
    }
}
