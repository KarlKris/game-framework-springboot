package com.li.gamesocket.server;

import com.li.gamesocket.channelhandler.NioNettyServerMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * 基于Netty的服务端Socket Bean
 */
@Component
@Slf4j
public class NioNettyServerBootstrap implements ApplicationRunner
        , ApplicationListener<ContextClosedEvent> {


    @Autowired
    private NioNettyServerMessageHandler nettyServerMessageHandler;
    @Autowired
    private ServerConfig serverConfig;


    /** NIO 线程组 **/
    private EventLoopGroup boss;
    private EventLoopGroup workers;
    /** 服务端channel **/
    private Channel channel;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.boss = new NioEventLoopGroup(serverConfig.getBossGroupThreadNum());
        this.workers = new NioEventLoopGroup(serverConfig.getNioGroupThreadNum());

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(this.boss, this.workers)
                .channel(NioServerSocketChannel.class)
                // ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数
                // 函数listen(int socketfd,int backlog)用来初始化服务端可连接队列
                // 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接
                //多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
                .option(ChannelOption.SO_BACKLOG, serverConfig.getBackLog())
                // 这个参数表示允许重复使用本地地址和端口
                // 某个服务器进程占用了TCP的80端口进行监听，此时再次监听该端口就会返回错误
                // 使用该参数就可以解决问题，该参数允许共用该端口，这个在服务器程序中比较常使用
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(this.nettyServerMessageHandler);

        // 同步绑定端口
        ChannelFuture channelFuture = serverBootstrap.bind(serverConfig.getPort()).sync();
        // 绑定服务器Channel
        this.channel = channelFuture.channel();

        if (log.isInfoEnabled()) {
            log.info("NIO Socket 服务器[{}]正常启动成功", serverConfig.getPort());
        }

    }


    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        this.boss.shutdownGracefully();
        this.workers.shutdownGracefully();
        this.channel.close();

        log.warn("NIO Socket 服务器[{}]正常关闭", serverConfig.getPort());
    }
}
