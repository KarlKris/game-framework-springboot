package com.li.core.cache.core.pubsub;

import com.li.core.cache.config.CachedType;
import com.li.core.cache.core.cache.impl.CaffeineRedisCache;
import com.li.core.cache.core.manager.CacheManager;
import com.li.core.redis.pubsub.AbstractPubSubMessageDelegate;
import com.li.core.redis.pubsub.PubSubConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * Redis缓存 发布/订阅消息
 * @author li-yuanwen
 */
@Slf4j
@Component
public class CacheOfPubSubMessageListener extends AbstractPubSubMessageDelegate<CacheOfPubSubMessage> {

    private final CacheManager cacheManager;

    public CacheOfPubSubMessageListener(@Autowired RedisMessageListenerContainer listenerContainer
            , @Autowired RedisTemplate<String, byte[]> redisTemplate, @Autowired CacheManager cacheManager) {
        super(listenerContainer, redisTemplate);
        this.cacheManager = cacheManager;
    }

    @Override
    public void handleMessage(CacheOfPubSubMessage body) {

        if (log.isDebugEnabled()) {
            log.debug("收到Redis[{}]发布的消息[{},{}]"
                    , getChannel()
                    , body.getCacheName(), body.getKey());
        }

        ((CaffeineRedisCache) cacheManager.getCache(CachedType.REMOTE, body.getCacheName())).removeLocalKey(body.getKey());
    }

    @Override
    public String getChannel() {
        return PubSubConstants.CACHE_EXPIRE_CHANNEL;
    }

}
