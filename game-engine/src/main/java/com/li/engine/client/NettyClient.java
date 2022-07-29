package com.li.engine.client;

import com.li.engine.service.rpc.invocation.Invocation;
import com.li.network.message.IMessage;

/**
 * @author li-yuanwen
 * @date 2021/8/1 17:45
 * Netty Client 接口
 **/
public interface NettyClient {

    /**
     * 发送消息
     * @param message       消息
     * @param invocation  invocation
     * @throws InterruptedException 连接不上对方时抛出
     */
     void send(IMessage message, Invocation invocation) throws InterruptedException;


    /**
     * 获取远程对象的代理
     *
     * @param clz 类对象
     * @param <T> 类
     * @return /
     */
    <T> T getSendProxy(Class<T> clz);

}
