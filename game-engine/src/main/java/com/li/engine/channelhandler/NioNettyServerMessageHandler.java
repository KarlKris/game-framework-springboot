package com.li.engine.channelhandler;

import com.li.common.shutdown.ShutdownProcessor;
import com.li.engine.channelhandler.common.NioNettyFilter;
import com.li.engine.channelhandler.common.impl.ProtocolSelectorHandler;
import com.li.engine.channelhandler.server.AbstractServerVocationalWorkHandler;
import com.li.engine.server.ServerConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.SSLEngine;
import java.util.List;

/**
 * netty 服务端childhandler
 * @author li-yuanwen
 */
@Slf4j
@Component
public class NioNettyServerMessageHandler extends ChannelInitializer<SocketChannel>
        implements FactoryBean<EventExecutorGroup>, ShutdownProcessor {

    @Resource
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    @Qualifier("serverSslContext")
    private SslContext sslContext;
    @Resource
    private List<NioNettyFilter> filters;
    @Resource
    private AbstractServerVocationalWorkHandler<?, ?> vocationalWorkHandler;
    @Resource
    private ServerConfig serverConfig;

    /** handler线程池 **/
    private EventExecutorGroup defaultEventExecutorGroup;

    @PostConstruct
    private void initialize() {
        // Reactor主从多线程模型中的handler模型
        defaultEventExecutorGroup = new DefaultEventExecutorGroup(serverConfig.getHandlerThreadNum()
                , new DefaultThreadFactory("Netty-Handler-Thread", true));
    }

    @Override
    public EventExecutorGroup getObject() throws Exception {
        return defaultEventExecutorGroup;
    }

    @Override
    public Class<?> getObjectType() {
        return EventExecutorGroup.class;
    }

    @Override
    public int getOrder() {
        return ShutdownProcessor.SHUT_DOWN_HANDLER;
    }

    @Override
    public void shutdown() {
        defaultEventExecutorGroup.shutdownGracefully();
        log.warn("优雅关闭Netty-Handler线程池");
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // SSL 认证
        if (this.sslContext != null) {
            SSLEngine sslEngine = sslContext.newEngine(socketChannel.alloc());
            pipeline.addFirst(defaultEventExecutorGroup, SslHandler.class.getSimpleName(), new SslHandler(sslEngine));
        }

        // 过滤器
        if (!CollectionUtils.isEmpty(filters)) {
            for (NioNettyFilter filter : this.filters) {
                pipeline.addLast(defaultEventExecutorGroup, filter.getName(), filter);
            }
        }

        // 协议选择
        pipeline.addLast(defaultEventExecutorGroup, ProtocolSelectorHandler.class.getSimpleName()
                , applicationContext.getBean(ProtocolSelectorHandler.class));

        // 心跳检测
        IdleStateHandler stateHandler = applicationContext.getBean("serverIdleStateHandler", IdleStateHandler.class);
        pipeline.addLast(defaultEventExecutorGroup, IdleStateHandler.class.getSimpleName(), stateHandler);

        // 业务
        pipeline.addLast(defaultEventExecutorGroup, AbstractServerVocationalWorkHandler.class.getSimpleName(), this.vocationalWorkHandler);

    }
}
