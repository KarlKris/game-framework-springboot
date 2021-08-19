package com.li.gamesocket.service.session;

import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.protocol.IMessage;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author li-yuanwen
 * 客户端连接Session管理
 */
@Component
@Slf4j
public class SessionManager {

    /** session id generator **/
    private final AtomicInteger sessionIdGenerator = new AtomicInteger(0);

    /** 未识别身份Session 字典 **/
    private final ConcurrentHashMap<Integer, Session> annoymous = new ConcurrentHashMap<>();

    /** 已识别身份Session 字典 **/
    private final ConcurrentHashMap<Long, Session> identities = new ConcurrentHashMap<>();

    /** 为Channel注册Session **/
    public Session registerSession(Channel channel) {
        int nextId = this.sessionIdGenerator.incrementAndGet();
        Session session = Session.newInstance(nextId, channel);
        this.annoymous.put(nextId, session);

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
        }else {
            this.annoymous.remove(session.getSessionId());
        }

        return session;
    }

    /** 是否在线 **/
    public boolean online(long identity) {
        return this.identities.containsKey(identity);
    }

    /** 绑定身份 **/
    public void bindIdentity(Session session, long identity, boolean inner) {
        Session remove = this.annoymous.remove(session.getSessionId());
        if (log.isDebugEnabled() && remove == null) {
            log.debug("session[{}]已绑定某个身份,本次绑定[{}]", session.getSessionId(), identity);
        }
        // 非内部连接才将身份标识绑定进Session
        if (!inner) {
            session.bind(identity);
        }

        Session oldSession = this.identities.put(identity, session);
        if (oldSession != null) {
            log.warn("玩家[{}]被顶号,强退账号", identity);
            oldSession.kick();
        }
    }

    /** 获取指定Session **/
    public Session getIdentitySession(long identity) {
        return this.identities.get(identity);
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
