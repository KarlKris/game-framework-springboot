package com.li.network.session;

import io.netty.channel.Channel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 内部连接
 * @author li-yuanwen
 * @date 2021/12/8
 */
public class ServerSession extends AbstractSession {

    /** 依附于该Session的PlayerSession#identity **/
    private final Set<Long> identities;

    public ServerSession(long sessionId, Channel channel) {
        super(sessionId, channel);
        this.identities = new HashSet<>();
    }

    @Override
    public void bindIdentity(long identity) {
        this.identities.add(identity);
    }

    public void logout(long identity) {
        this.identities.remove(identity);
    }

    public Set<Long> getIdentities() {
        return Collections.unmodifiableSet(identities);
    }
}
