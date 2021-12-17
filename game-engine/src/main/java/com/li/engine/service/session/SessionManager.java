package com.li.engine.service.session;

import com.li.network.message.IMessage;
import com.li.network.protocol.ChannelAttributeKeys;
import com.li.network.session.ISession;
import com.li.network.session.PlayerSession;
import com.li.network.session.ServerSession;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author li-yuanwen
 * 客户端连接Session管理
 */
@Component
@Slf4j
public class SessionManager {

    /** session id generator **/
    private final AtomicLong sessionIdGenerator = new AtomicLong(0);

    /** Identity2Session 字典 **/
    private final ConcurrentHashMap<Long, ISession> identities = new ConcurrentHashMap<>();

    /** 为Channel注册PlayerSession **/
    public PlayerSession registerPlayerSession(Channel channel) {
        long nextId = this.sessionIdGenerator.incrementAndGet();
        PlayerSession playerSession = new PlayerSession(nextId, channel);
        // channel绑定属性
        channel.attr(ChannelAttributeKeys.SESSION).set(playerSession);
        return playerSession;
    }

    /** 为Channel注册PlayerSession **/
    public ServerSession registerServerSession(Channel channel) {
        long nextId = this.sessionIdGenerator.incrementAndGet();
        ServerSession serverSession = new ServerSession(nextId, channel);
        // channel绑定属性
        channel.attr(ChannelAttributeKeys.SESSION).set(serverSession);
        return serverSession;
    }


    /** 删除为Channel注册的Session **/
    public PlayerSession removePlayerSession(Channel channel) {
        PlayerSession session = (PlayerSession) channel.attr(ChannelAttributeKeys.SESSION).get();
        if (session == null) {
            return null;
        }

        if (session.isIdentity()) {
            this.identities.remove(session.getIdentity());
        }

        return session;
    }

    /** 删除为Channel注册的Session **/
    public ServerSession removeServerSession(Channel channel) {
        ServerSession session = (ServerSession) channel.attr(ChannelAttributeKeys.SESSION).get();
        if (session == null) {
            return null;
        }

        for (long identity : session.getIdentities()) {
            identities.remove(identity);
        }

        return session;
    }

    /** 是否在线 **/
    public boolean online(Long identity) {
        return this.identities.containsKey(identity);
    }

    /**
     * 绑定身份
     * @param session 连接Session
     * @param identity 身份标识
     * @return 旧Session or null
     */
    public ISession bindIdentity(ISession session, long identity) {

        if (log.isDebugEnabled()) {
            log.debug("session[{}]绑定某个身份[{}]", session.getSessionId(), identity);
        }

        if (session instanceof PlayerSession) {
            ((PlayerSession) session).bindIdentity(identity);
        }

        ISession oldSession = this.identities.put(identity, session);
        if (oldSession != null && !Objects.equals(session.getSessionId(), oldSession.getSessionId())) {
            if (log.isDebugEnabled()) {
                log.debug("玩家[{}]被顶号", identity);
            }

            return oldSession;
        }

        return null;

    }

    /** 断开连接 **/
    public void kickOut(ISession session) {
        session.close();
    }

    /** 断开连接 **/
    public void kickOut(long identity) {
        ISession session = this.getIdentitySession(identity);
        if (session != null) {
            session.close();
        }
    }

    /** 获取指定Session **/
    public ISession getIdentitySession(long identity) {
        return this.identities.get(identity);
    }

    /** 获取已绑定身份的标识集 **/
    public Collection<Long> getOnlineIdentities() {
        return Collections.unmodifiableCollection(this.identities.keySet());
    }


    /** 写入Channel **/
    public static void writeAndFlush(ISession session, IMessage message) {
        if (message == null) {
            if (log.isWarnEnabled()) {
                log.warn("向连接[{}]写入null信息,忽略", session.getIp());
            }
            return;
        }


        session.writeAndFlush(message);
    }


}
