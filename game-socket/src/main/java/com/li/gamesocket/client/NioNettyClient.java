package com.li.gamesocket.client;

import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.protocol.serialize.Serializer;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author li-yuanwen
 * @date 2021/8/1 17:45
 * Netty Client 接口
 **/
public interface NioNettyClient {

    /**
     * 发送消息
     * @param message 消息
     * @param sendSuccessConsumer 发送成功后执行函数
     * @return /
     *
     * @exception InterruptedException 连接不上对方时抛出
     */
    <T> CompletableFuture<T> send(IMessage message
            , BiConsumer<IMessage, CompletableFuture<T>> sendSuccessConsumer) throws InterruptedException;


    /**
     * 获取远程对象的代理
     * @param clasz 类对象
     * @param <T> 类
     * @return /
     */
    <T> T getSendProxy(Class<T> clasz);

}
