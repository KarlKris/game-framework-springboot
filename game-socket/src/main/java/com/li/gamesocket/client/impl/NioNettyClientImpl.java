package com.li.gamesocket.client.impl;

import com.li.gamecore.rpc.model.Address;
import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.client.NioNettyClient;
import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.protocol.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author li-yuanwen
 * Netty Client
 */
@Slf4j
public class NioNettyClientImpl implements NioNettyClient {

    /** 连接目标IP地址 **/
    private Address address;
    /** 连接超时(毫秒) **/
    private int connectTimeoutMillis;
    /** 共享线程组 **/
    private EventLoopGroup eventLoopGroup;
    /** ChannelInitializer **/
    private ChannelInitializer channelInitializer;

    /** Channel **/
    private Channel channel;

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
            , EventLoopGroup eventLoopGroup, ChannelInitializer channelInitializer) {
        NioNettyClientImpl nioNettyClient = new NioNettyClientImpl();
        nioNettyClient.address = address;
        nioNettyClient.connectTimeoutMillis = connectTimeoutMillis;
        nioNettyClient.eventLoopGroup = eventLoopGroup;
        nioNettyClient.channelInitializer = channelInitializer;
        return nioNettyClient;
    }
}
