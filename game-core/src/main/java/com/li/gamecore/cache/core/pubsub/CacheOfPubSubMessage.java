package com.li.gamecore.cache.core.pubsub;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Redis 缓存 发布/订阅消息
 * @author li-yuanwen
 */
@Getter
@NoArgsConstructor
@ToString
public class CacheOfPubSubMessage implements Serializable {

    private static final long serialVersionUID = -6984960228570646162L;

    /** 缓存名 **/
    private String cacheName;
    /** key **/
    private String key;

    public CacheOfPubSubMessage(String cacheName, String key) {
        this.cacheName = cacheName;
        this.key = key;
    }
}
