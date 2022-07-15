package com.li.network.session;

import com.li.common.concurrency.DefaultRunnableSource;
import com.li.common.concurrency.RunnableLoop;
import com.li.common.concurrency.RunnableSource;
import com.li.common.utils.IpUtils;
import com.li.network.message.IMessage;
import com.li.network.protocol.ChannelAttributeKeys;
import io.netty.channel.Channel;

/**
 * ISession抽象基类
 * @author li-yuanwen
 * @date 2021/12/8
 */
public abstract class AbstractSession implements ISession {

    private final RunnableSource source;

    /** session唯一标识 **/
    protected final long sessionId;
    /** 连接Channel **/
    protected final Channel channel;

    AbstractSession(long sessionId, Channel channel) {
        this.sessionId = sessionId;
        this.channel = channel;
        this.source = new DefaultRunnableSource();
    }


    @Override
    public void writeAndFlush(IMessage message) {
        channel.writeAndFlush(message);
    }

    @Override
    public String getIp() {
        return IpUtils.getIp(this.channel.remoteAddress());
    }

    @Override
    public long getSessionId() {
        return sessionId;
    }

    @Override
    public void close() {
        this.channel.close();
    }

    @Override
    public void setSerializeType(Byte serializeType) {
        this.channel.attr(ChannelAttributeKeys.LAST_SERIALIZE_TYPE).set(serializeType);
    }

    @Override
    public Byte getSerializeType() {
        return this.channel.attr(ChannelAttributeKeys.LAST_SERIALIZE_TYPE).get();
    }

    @Override
    public RunnableLoop runnableLoop() {
        return source.runnableLoop();
    }

    @Override
    public void register(RunnableLoop runnableLoop) {
        source.register(runnableLoop);
    }

    @Override
    public boolean isRegisterRunnableLoop() {
        return source.isRegisterRunnableLoop();
    }

}
