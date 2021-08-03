package com.li.gamesocket.client.impl;

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
@AllArgsConstructor
public class NioNettyClientImpl {

    /** 连接超时(毫秒) **/
    private int connectTimeoutMillis;
    /** Channel **/
    private Channel channel;
    /** 共享线程组 **/
    private EventLoopGroup eventLoopGroup;
    /** ChannelInitializer **/
    private ChannelInitializer channelInitializer;

    private void connect(String ip, int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.connectTimeoutMillis)
                .handler(this.channelInitializer);

        ChannelFuture future = bootstrap.connect(ip, port).sync();
        this.channel = future.channel();

        log.warn("客户端连接[{}:{}]成功", ip, port);

    }



}
