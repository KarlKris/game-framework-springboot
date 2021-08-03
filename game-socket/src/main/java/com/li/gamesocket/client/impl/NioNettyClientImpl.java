package com.li.gamesocket.client.impl;

import com.li.gamecore.rpc.model.Address;
import com.li.gamesocket.client.NioNettyClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
