package com.li.engine.client.impl;

import com.li.common.ApplicationContextHolder;
import com.li.common.rpc.model.Address;
import com.li.engine.channelhandler.NioNettyClientMessageHandler;
import com.li.engine.client.*;
import com.li.engine.protocol.MessageFactory;
import com.li.engine.service.VocationalWorkConfig;
import com.li.engine.service.rpc.InvocationManager;
import com.li.engine.service.rpc.invocation.Invocation;
import com.li.network.anno.SocketController;
import com.li.network.message.IMessage;
import com.li.network.protocol.SocketProtocolManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Netty Client
 * @author li-yuanwen
 */
@Slf4j
public class DefaultNettyClient implements NettyClient {

    /** 连接目标IP地址 **/
    private final Address address;
    /** 连接超时(毫秒) **/
    private final int connectTimeoutMillis;
    /** 共享线程组 **/
    private final EventLoopGroup eventLoopGroup;
    /** ChannelInitializer **/
    private final NioNettyClientMessageHandler messageHandler;
    /** rpc消息容器 **/
    private final InvocationManager invocationManager;
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

    public DefaultNettyClient(Address address, int connectTimeoutMillis
            , EventLoopGroup eventLoopGroup
            , NioNettyClientMessageHandler messageHandler
            , InvocationManager invocationManager
            , MessageFactory messageFactory
            , SocketProtocolManager socketProtocolManager
            , int timeoutSecond) {
        this.address = address;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.eventLoopGroup = eventLoopGroup;
        this.messageHandler = messageHandler;
        this.invocationManager = invocationManager;
        this.messageFactory = messageFactory;
        this.socketProtocolManager = socketProtocolManager;
        this.timeoutSecond = timeoutSecond;
    }

    @Override
    public void send(IMessage message, Invocation invocation) throws InterruptedException {
        if (!isConnected()) {
            connect();
        }

        channel.writeAndFlush(message);
        invocationManager.addInvocation(invocation);
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
                    , new SendProxyInvoker(this, invocationManager, messageFactory, socketProtocolManager, timeoutSecond));

            this.proxy.put(name, target);
        }

        return (T) target;
    }

    private void connect() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT)
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



    public static DefaultNettyClient newInstance(Address address, int connectTimeoutMillis
            , EventLoopGroup eventLoopGroup) {
        InvocationManager invocationManager = ApplicationContextHolder.getBean(InvocationManager.class);
        MessageFactory messageFactory = ApplicationContextHolder.getBean(MessageFactory.class);
        VocationalWorkConfig config = ApplicationContextHolder.getBean(VocationalWorkConfig.class);
        NioNettyClientMessageHandler messageHandler = ApplicationContextHolder.getBean(NioNettyClientMessageHandler.class);
        SocketProtocolManager socketProtocolManager = ApplicationContextHolder.getBean(SocketProtocolManager.class);
        return new DefaultNettyClient(address, connectTimeoutMillis
                , eventLoopGroup, messageHandler
                , invocationManager, messageFactory
                , socketProtocolManager, config.getTimeoutSecond());
    }
}
