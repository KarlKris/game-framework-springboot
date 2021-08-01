package com.li.gamesocket.session;

import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.protocol.IMessage;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    private AtomicInteger sessionIdGenerator = new AtomicInteger(0);

    /** 未识别身份Session 字典 **/
    private ConcurrentHashMap<Integer, Session> annoymous = new ConcurrentHashMap<>();

    /** 已识别身份Session 字典 **/
    private ConcurrentHashMap<Long, Session> identities = new ConcurrentHashMap<>();


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
        if (session.identity()) {
            this.identities.remove(session.getIdentity());
        }else {
            this.annoymous.remove(session.getSessionId());
        }

        return session;
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
