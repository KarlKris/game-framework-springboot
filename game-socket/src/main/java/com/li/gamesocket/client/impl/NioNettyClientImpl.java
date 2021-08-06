package com.li.gamesocket.client.impl;

import com.li.gamecore.ApplicationContextHolder;
import com.li.gamecore.rpc.model.Address;
import com.li.gamesocket.client.NioNettyClient;
import com.li.gamesocket.client.SendProxyInvoker;
import com.li.gamesocket.client.channelhandler.NioNettyClientMessageHandler;
import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.service.command.MethodCtx;
import com.li.gamesocket.utils.CommandUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
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
    private final ChannelInitializer channelInitializer = ApplicationContextHolder.getBean(NioNettyClientMessageHandler.class);

    /** 代理对象 **/
    private final Map<String, Object> proxy = new HashMap<>();

    /** Channel **/
    private Channel channel;

    NioNettyClientImpl(Address address, int connectTimeoutMillis
            , EventLoopGroup eventLoopGroup) {
        this.address = address;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.eventLoopGroup = eventLoopGroup;
    }


    @Override
    public <T> CompletableFuture<T> send(IMessage message, BiConsumer<IMessage, CompletableFuture<T>> sendSuccessConsumer)
            throws InterruptedException {

        if (!isConnected()) {
            connect();
        }

        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        ChannelFuture channelFuture = channel.writeAndFlush(message);
        channelFuture.addListener(future -> {
            if (future.cause() != null) {
                sendSuccessConsumer.accept(message, completableFuture);
            }else {
                log.error("向服务器[{}]发送信息发生异常", address, future.cause());
                completableFuture.completeExceptionally(future.cause());
            }
        });

        return completableFuture;
    }


    @Override
    public <T> T getSendProxy(Class<T> clasz) {

        String name = clasz.getName();
        Object target = this.proxy.get(name);
        if (target != null) {
            return (T) target;
        }

        synchronized (this.proxy) {
            target = this.proxy.get(name);
            if (target != null) {
                return (T) target;
            }

            List<MethodCtx> methodCtx = CommandUtils.analysisCommands(clasz, false);
            target = Proxy.newProxyInstance(clasz.getClassLoader()
                    , new Class[]{clasz}
                    , new SendProxyInvoker(this, methodCtx));

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
                .handler(this.channelInitializer);

        ChannelFuture future = bootstrap.connect(this.address.getIp(), this.address.getPort()).sync();
        this.channel = future.channel();

        log.warn("客户端连接[{}:{}]成功", this.address.getIp(), this.address.getPort());

    }

    private boolean isConnected() {
        return this.channel != null && this.channel.isActive();
    }



    public static NioNettyClientImpl newInstance(Address address, int connectTimeoutMillis
            , EventLoopGroup eventLoopGroup) {
        return new NioNettyClientImpl(address, connectTimeoutMillis, eventLoopGroup);
    }
}
