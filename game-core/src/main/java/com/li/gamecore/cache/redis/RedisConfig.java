package com.li.gamecore.cache.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.gamecore.cache.redis.pubsub.CacheOfPubSubMessageListener;
import com.li.gamecore.cache.redis.pubsub.PubSubConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * @author li-yuanwen
 * Redis 属性配置类
 */
@Configuration
public class RedisConfig {

    @Bean
    public Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer(ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<Object> redisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisSerializer.setObjectMapper(objectMapper);
        return redisSerializer;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory
            , Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer(jackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setDefaultSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }


    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory
            , CacheOfPubSubMessageListener cacheOfPubSubMessageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(cacheOfPubSubMessageListener, new ChannelTopic(PubSubConstants.CACHE_CHANNEL));
        return container;
    }

}
