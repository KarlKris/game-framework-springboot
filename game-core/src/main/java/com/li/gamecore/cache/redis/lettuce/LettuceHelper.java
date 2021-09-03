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
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Set<String> getAll(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public void set(String key, Object value, int seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void del(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void delAll(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        redisTemplate.delete(keys);
    }

    @Override
    public String type(String key) {
        DataType type = redisTemplate.type(key);
        return type == null ? null : type.name();
    }

    @Override
    public Boolean expire(String key, int seconds) {
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    @Override
    public Boolean expireAt(String key, long unixTime) {
        return redisTemplate.expireAt(key, new Date(unixTime));
    }

    @Override
    public Long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    @Override
    public Object getAndSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    @Override
    public void hSet(String key, Object field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public Object hGet(String key, Object field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    @Override
    public void hDel(String key, Object field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    @Override
    public boolean setnx(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    @Override
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public void sAdd(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    @Override
    public Set<?> sAll(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public boolean sDel(String key, Object value) {
        return redisTemplate.opsForSet().remove(key, value) == 1;
    }

    @Override
    public void publish(String channel, Object msg) {
        redisTemplate.convertAndSend(channel, msg);
    }
}
