package com.li.network.session;


import com.li.common.concurrency.RunnableSource;
import com.li.network.message.IMessage;

/**
 * 网络层连接对象接口
 * @author li-yuanwen
 * @date 2021/12/8
 */
public interface ISession extends RunnableSource {


    /**
     * 获取连接对象唯一标识
     * @return 唯一标识
     */
    long getSessionId();

    /**
     * 获取连接对象的ip地址
     * @return ip地址
     */
    String getIp();

    /**
     * 传输消息
     * @param message 消息
     */
    void writeAndFlush(IMessage message);

    /**
     * 关闭连接
     */
    void close();

    /**
     * 设置最近的传输的序列化方式
     * @param serializeType 序列化方式
     */
    void setSerializeType(Byte serializeType);


    /**
     * 获取最近传输的序列化方式
     * @return 序列化方式 or null
     */
    Byte getSerializeType();

    /**
     * 绑定身份标识
     * @param identity 身份标识
     * @return PlayerSession
     */
    PlayerSession bindIdentity(long identity);

}
