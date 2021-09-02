package com.li.gamecore.cache.redis.pubsub;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author li-yuanwen
 * Redis 缓存 发布/订阅消息
 */
@Getter
@NoArgsConstructor
public class CacheOfPubSubMessage implements Serializable {

    private static final long serialVersionUID = -6984960228570646162L;

    /** 缓存名 **/
    private String cacheName;
    /** key **/
    private Object key;

    public CacheOfPubSubMessage(String cacheName, Object key) {
        this.cacheName = cacheName;
        this.key = key;
    }
}
