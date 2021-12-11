package com.li.gamesocket.service.session;

import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Set;

/**
 * 内部连接
 * @author li-yuanwen
 * @date 2021/12/8
 */
public class ServerSession extends AbstractSession {

    /** 依附于该Session的PlayerSession#identity **/
    private Set<Long> identities;

    public ServerSession(long sessionId, Channel channel) {
        super(sessionId, channel);
        this.identities = new HashSet<>();
    }

    @Override
    public void bindIdentity(long identity) {
        this.identities.add(identity);
    }

    public boolean isIdentity() {
        // 没有唯一的身份标识
        return false;
    }

    public long getIdentity() {
        return 0;
    }
}
