package com.li.engine.client.impl;

import com.li.common.ApplicationContextHolder;
import com.li.common.rpc.model.Address;
import com.li.engine.channelhandler.NioNettyClientMessageHandler;
import com.li.engine.client.NioNettyClient;
import com.li.engine.client.SendProxyInvoker;
import com.li.engine.protocol.MessageFactory;
import com.li.engine.service.VocationalWorkConfig;
import com.li.engine.service.rpc.SocketFutureManager;
import com.li.network.anno.SocketController;
import com.li.network.message.IMessage;
import com.li.network.protocol.SocketProtocolManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author li-yuanwen
 * Netty Client
 */
@Slf4j
public class NioNettyClientImpl implements NioNettyClient {

    /** 连接目标IP地址 **/
    private final Address address;
    /** 连接超时(毫秒) **/
    private final int connectTimeoutMillis;
    /** 共享线程组 **/
    private final EventLoopGroup eventLoopGroup;
    /** ChannelInitializer **/
    private final NioNettyClientMessageHandler messageHandler;
    /** rpc消息容器 **/
    private final SocketFutureManager socketFutureManager;
    /** 消息工厂 **/
    private final MessageFactory messageFactory;
    /** 协议容器 **/
    private final SocketProtocolManager socketProtocolManager;
    /** 超时时间 **/
    private final int timeoutSecond;

    /** 代理对象 **/
    private final Map<String, Object> proxy = new HashMap<>();

    /** Channel **/
    private Channel channel;

    public NioNettyClientImpl(Address address, int connectTimeoutMillis
            , EventLoopGroup eventLoopGroup
            , NioNettyClientMessageHandler messageHandler
            , SocketFutureManager socketFutureManager
            , MessageFactory messageFactory
            , SocketProtocolManager socketProtocolManager
            , int timeoutSecond) {
        this.address = address;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.eventLoopGroup = eventLoopGroup;
        this.messageHandler = messageHandler;
        this.socketFutureManager = socketFutureManager;
        this.messageFactory = messageFactory;
        this.socketProtocolManager = socketProtocolManager;
        this.timeoutSecond = timeoutSecond;
    }

    @Override
    public <T> CompletableFuture<T> send(IMessage message, BiConsumer<IMessage, CompletableFuture<T>> sendSuccessConsumer)
            throws InterruptedException {

        if (!isConnected()) {
            connect();
        }

        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        // 这里不是使用的writeAndFlush的原因是防止消息写完时,监听器还未添加
        ChannelFuture channelFuture = channel.write(message);
        channelFuture.addListener(future -> {
            Throwable cause = future.cause();
            if (cause == null) {
                sendSuccessConsumer.accept(message, completableFuture);
            }else {
                log.error("向服务器[{}:{}]发送信息发生异常", address.getIp(), address.getPort(), cause);
                completableFuture.completeExceptionally(cause);
            }
        });
        channel.flush();

        return completableFuture;
    }


    @Override
    public <T> T getSendProxy(Class<T> clz) {

        String name = clz.getName();
        Object target = this.proxy.get(name);
        if (target != null) {
            return (T) target;
        }

        synchronized (this.proxy) {
            target = this.proxy.get(name);
            if (target != null) {
                return (T) target;
            }

            SocketController socketController = AnnotationUtils.findAnnotation(clz, SocketController.class);
            if (socketController == null) {
                throw new RuntimeException(clz.getSimpleName() + "不是远程协议接口");
            }

            target = Proxy.newProxyInstance(clz.getClassLoader()
                    , new Class[]{clz}
                    , new SendProxyInvoker(this, socketFutureManager, messageFactory, socketProtocolManager, timeoutSecond));

            this.proxy.put(name, target);
        }

        return (T) target;
    }

    private void connect() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.connectTimeoutMillis)
                .handler(this.messageHandler);

        ChannelFuture channelFuture = bootstrap.connect(this.address.getIp(), this.address.getPort());
        this.channel = channelFuture.channel();
        channelFuture.sync();


        log.warn("客户端连接[{}:{}]成功", this.address.getIp(), this.address.getPort());

    }

    private boolean isConnected() {
        return this.channel != null && this.channel.isActive();
    }



    public static NioNettyClientImpl newInstance(Address address, int connectTimeoutMillis
            , EventLoopGroup eventLoopGroup) {
        SocketFutureManager socketFutureManager = ApplicationContextHolder.getBean(SocketFutureManager.class);
        MessageFactory messageFactory = ApplicationContextHolder.getBean(MessageFactory.class);
        VocationalWorkConfig config = ApplicationContextHolder.getBean(VocationalWorkConfig.class);
        NioNettyClientMessageHandler messageHandler = ApplicationContextHolder.getBean(NioNettyClientMessageHandler.class);
        SocketProtocolManager socketProtocolManager = ApplicationContextHolder.getBean(SocketProtocolManager.class);
        return new NioNettyClientImpl(address, connectTimeoutMillis
                , eventLoopGroup, messageHandler
                , socketFutureManager, messageFactory
                , socketProtocolManager, config.getTimeoutSecond());
    }
}
