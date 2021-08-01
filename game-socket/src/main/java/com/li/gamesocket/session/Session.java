package com.li.gamesocket.session;

import cn.hutool.core.net.Ipv4Util;
import com.li.gamesocket.protocol.IMessage;
import io.netty.channel.Channel;
import lombok.Getter;

/**
 * @author li-yuanwen
 * 连接Session封装
 */
@Getter
public class Session {

    /** session标识 **/
    private int sessionId;
    /** 身份标识 **/
    private long identity;
    /** channel **/
    private Channel channel;

    public boolean identity() {
        return identity > 0;
    }

    public String ip() {
        return null;
    }


    /** 绑定标识 **/
    void bind(long identity) {
        this.identity = identity;
    }

    /** 写入消息 **/
    void writeAndFlush(IMessage message) {
        channel.writeAndFlush(message);
    }

    static Session newInstance(int sessionId, Channel channel) {
        Session session = new Session();
        session.sessionId = sessionId;
        session.channel = channel;
        return session;
    }

}
