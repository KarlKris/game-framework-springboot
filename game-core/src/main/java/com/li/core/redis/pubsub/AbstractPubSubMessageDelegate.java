package com.li.core.redis.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

/**
 * @author li-yuanwen
 * @date 2022/1/5
 */
@Slf4j
public abstract class AbstractPubSubMessageDelegate<B> implements MessageListener, InitializingBean, DisposableBean {

    private final RedisMessageListenerContainer listenerContainer;
    private final RedisSerializer<?> redisSerializer;

    public AbstractPubSubMessageDelegate(RedisMessageListenerContainer listenerContainer, RedisTemplate<String, byte[]> redisTemplate) {
        Assert.notNull(listenerContainer, "RedisMessageListenerContainer to run in must not be null!");
        this.listenerContainer = listenerContainer;
        this.redisSerializer = redisTemplate.getValueSerializer();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] body = message.getBody();
        B obj = (B) redisSerializer.deserialize(body);

        if (log.isDebugEnabled()) {
            log.debug("Class:{} 处理订阅channel[{}]消息[{}-{}]"
                    , getClass().getSimpleName()
                    , getChannel()
                    , obj == null ? "null" : obj.getClass().getSimpleName()
                    , obj);
        }

        handleMessage(obj);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 订阅
        listenerContainer.addMessageListener(this, new ChannelTopic(getChannel()));
    }

    @Override
    public void destroy() throws Exception {
        // 退订
        listenerContainer.removeMessageListener(this);
    }

    /**
     * redis订阅channel
     * @return PubSubChannel
     */
    public abstract String getChannel();

    /**
     * redis订阅内容处理
     * @param body 订阅内容
     */
    public abstract void handleMessage(B body);

}
