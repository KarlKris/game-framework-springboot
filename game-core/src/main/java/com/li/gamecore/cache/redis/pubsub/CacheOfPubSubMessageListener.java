package com.li.gamecore.cache.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.gamecore.cache.config.CachedType;
import com.li.gamecore.cache.core.manager.CacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author li-yuanwen
 * Redis 发布/订阅消息
 */
@Slf4j
@Component
public class CacheOfPubSubMessageListener implements MessageListener {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        byte[] body = message.getBody();
        CacheOfPubSubMessage msg = null;
        try {
            msg = objectMapper.readValue(body, CacheOfPubSubMessage.class);
        } catch (IOException e) {
            log.error("Redis 反序列化CacheOfPubSubMessage对象发送未知异常", e);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("收到Channel[{}]发布的消息[{},{}]"
                    , new String(message.getChannel(), StandardCharsets.UTF_8)
                    , msg.getCacheName(), msg.getKey());
        }

        cacheManager.getCache(CachedType.REMOTE, msg.getCacheName()).remove(msg.getKey());
    }
}
