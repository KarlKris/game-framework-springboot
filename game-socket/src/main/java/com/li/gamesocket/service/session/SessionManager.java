package com.li.gamesocket.service.session;

import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.protocol.IMessage;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author li-yuanwen
 * 客户端连接Session管理
 */
@Component
@Slf4j
public class SessionManager {

    /** session id generator **/
    private final AtomicInteger sessionIdGenerator = new AtomicInteger(0);

    /** SessionId2Session 字典 **/
    private final ConcurrentHashMap<Integer, Session> sessions = new ConcurrentHashMap<>();

    /** Identity2Session 字典 **/
    private final ConcurrentHashMap<Long, Integer> identities = new ConcurrentHashMap<>();

    /** 为Channel注册Session **/
    public Session registerSession(Channel channel) {
        int nextId = this.sessionIdGenerator.incrementAndGet();
        Session session = Session.newInstance(nextId, channel);
        this.sessions.put(nextId, session);

        // channel绑定属性
        channel.attr(ChannelAttributeKeys.SESSION).set(session);

        return session;
    }


    /** 删除为Channel注册的Session **/
    public Session removeSession(Channel channel) {
        Session session = channel.attr(ChannelAttributeKeys.SESSION).get();
        if (session == null) {
            return null;
        }

        if (session.identity()) {
            this.identities.remove(session.getIdentity());
        }

        this.sessions.remove(session.getSessionId());

        return session;
    }

    /** 是否在线 **/
    public boolean online(long identity) {
        return this.identities.containsKey(identity);
    }

    /**
     * 绑定身份
     * @param session 连接Session
     * @param identity 身份标识
     * @return null 身份标识第一次绑定,若是顶号,则返回旧Session
     */
    public Session bindIdentity(Session session, long identity) {

        if (log.isDebugEnabled()) {
            log.debug("session[{}]绑定某个身份[{}]", session.getSessionId(), identity);
        }

        Integer oldSessionId = this.identities.put(identity, session.getSessionId());
        if (oldSessionId != null && !Objects.equals(session.getSessionId(), oldSessionId)) {
            log.warn("玩家[{}]被顶号", identity);
            return this.sessions.remove(oldSessionId);
        }
        return null;
    }

    /** 获取指定Session **/
    public Session getIdentitySession(long identity) {
        return this.sessions.get(this.identities.get(identity));
    }

    /** 获取已绑定身份的标识集 **/
    public Collection<Long> getOnlineIdentities() {
        return Collections.unmodifiableCollection(this.identities.keySet());
    }


    /** 写入Channel **/
    public void writeAndFlush(Session session, IMessage message) {
        if (message == null) {
            if (log.isWarnEnabled()) {
                log.warn("向连接[{}]写入null信息,忽略", session.getChannel().remoteAddress());
            }
            return;
        }
        session.writeAndFlush(message);
    }


}
