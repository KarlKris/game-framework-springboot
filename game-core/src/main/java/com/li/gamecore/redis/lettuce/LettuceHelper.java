package com.li.gamecore.redis.lettuce;

import cn.hutool.core.util.ArrayUtil;
import com.li.gamecommon.utils.ProtoStuffUtils;
import com.li.gamecore.redis.DistributedCacheManager;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基于Lettuce的工具类
 * @author li-yuanwen
 */
@Component
public class LettuceHelper implements DistributedCacheManager {


    @Resource
    private RedisTemplate<String, byte[]> redisTemplate;

    @Override
    public <T> T get(String key, Class<T> tClass) {
        return deserialize(redisTemplate.opsForValue().get(key), tClass);
    }

    @Override
    public Set<String> getAll(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public void set(String key, Object value, int seconds) {
        redisTemplate.opsForValue().set(key, ProtoStuffUtils.serialize(value)
                , seconds, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, ProtoStuffUtils.serialize(value));
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
    public <T> T getAndSet(final String key, final T value) {
        byte[] bytes = redisTemplate.opsForValue().getAndSet(key, ProtoStuffUtils.serialize(value));
        return deserialize(bytes, (Class<T>) value.getClass());
    }

    @Override
    public void hSet(String key, Object field, Object value) {
        redisTemplate.opsForHash().put(key, field, ProtoStuffUtils.serialize(value));
    }

    @Override
    public <T> T hGet(String key, Object field, Class<T> tClass) {
        StringRedisSerializer keySerializer = StringRedisSerializer.UTF_8;
        RedisSerializer<Object> hashKeySerializer = (RedisSerializer<Object>) redisTemplate.getHashKeySerializer();
        final byte[] keyBytes = keySerializer.serialize(key);
        final byte[] hashKeyBytes = hashKeySerializer.serialize(field);
        return redisTemplate.execute((RedisCallback<T>) connection -> deserialize(connection.hGet(keyBytes, hashKeyBytes), tClass));
    }

    @Override
    public void hDel(String key, Object field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    @Override
    public boolean setnx(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, ProtoStuffUtils.serialize(value)));
    }

    @Override
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public void sAdd(String key, Object value) {
        redisTemplate.opsForSet().add(key, ProtoStuffUtils.serialize(value));
    }

    @Override
    public <T> Set<T> sAll(String key, Class<T> tClass) {
        Set<byte[]> members = redisTemplate.opsForSet().members(key);
        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptySet();
        }
        return members.stream()
                .map(bytes -> deserialize(bytes, tClass))
                .filter(obj -> !Objects.isNull(obj))
                .collect(Collectors.toSet());

    }

    @Override
    public boolean sDel(String key, Object value) {
        Long remove = redisTemplate.opsForSet().remove(key, ProtoStuffUtils.serialize(value));
        return Objects.equals(remove, 1L);
    }

    @Override
    public void publish(String channel, Object msg) {
        redisTemplate.convertAndSend(channel, ProtoStuffUtils.serialize(msg));
    }

    // ---------------------------------------------------------------------------

    private <T> T deserialize(final byte[] body, final Class<T> tClass) {
        if (ArrayUtil.isNotEmpty(body)) {
            return ProtoStuffUtils.deserialize(body, tClass);
        }
        return null;
    }
}
