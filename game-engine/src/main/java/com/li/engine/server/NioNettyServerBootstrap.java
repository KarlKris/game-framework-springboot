package com.li.engine.server;

import com.li.common.shutdown.ShutdownProcessor;
import com.li.common.utils.IpUtils;
import com.li.engine.channelhandler.NioNettyServerMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 基于Netty的服务端Socket Bean
 * @author li-yuanwen
 */
@Component
@Slf4j
public class NioNettyServerBootstrap implements ApplicationRunner
        , ApplicationListener<ContextClosedEvent> {


    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private NioNettyServerMessageHandler nettyServerMessageHandler;
    @Resource
    private ServerConfig serverConfig;


    /** NIO 线程组 **/
    private EventLoopGroup boss;
    private EventLoopGroup workers;
    /** 服务端channel **/
    private Channel channel;

    private List<ShutdownProcessor> shutdownHooks = new LinkedList<>();

    @PostConstruct
    private void initialize() {
        shutdownHooks.addAll(applicationContext.getBeansOfType(ShutdownProcessor.class).values());
        shutdownHooks.sort(Comparator.comparingInt(ShutdownProcessor::getOrder));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.boss = createLoopGroup(1, "Netty-Boss-Thread");
        this.workers = createLoopGroup(serverConfig.getNioGroupThreadNum(), "Netty-Worker-Thread");

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(this.boss, this.workers)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                // ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数
                // 函数listen(int socketfd,int backlog)用来初始化服务端可连接队列
                // 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接
                // 多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
                .option(ChannelOption.SO_BACKLOG, serverConfig.getBackLog())
                // 这个参数表示允许重复使用本地地址和端口
                // 某个服务器进程占用了TCP的80端口进行监听，此时再次监听该端口就会返回错误
                // 使用该参数就可以解决问题，该参数允许共用该端口，这个在服务器程序中比较常使用
                .option(ChannelOption.SO_REUSEADDR, true)
                // 重用缓存区
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(this.nettyServerMessageHandler);

        // 同步绑定端口
        ChannelFuture channelFuture = serverBootstrap.bind(serverConfig.getPort()).sync();
        // 绑定服务器Channel
        this.channel = channelFuture.channel();

        if (log.isInfoEnabled()) {
            log.info("Netty 服务器[{}]正常启动成功", serverConfig.getPort());
        }

    }

    private boolean useEpoll() {
        return IpUtils.isLinuxPlatform() && Epoll.isAvailable();
    }

    private EventLoopGroup createLoopGroup(int threadNum, String threadPrefix) {
        if (useEpoll()) {
            return new EpollEventLoopGroup(threadNum, new DefaultThreadFactory(threadPrefix, true));
        }
        return new NioEventLoopGroup(threadNum, new DefaultThreadFactory(threadPrefix, true));
    }


    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        try {
            this.boss.shutdownGracefully();
            this.workers.shutdownGracefully();
            this.channel.close();
            log.warn("NIO Socket 服务器[{}]正常关闭", serverConfig.getPort());
        } finally {
            // 关闭
            shutdownHooks.forEach(ShutdownProcessor::shutdown);
        }

    }
}
