package com.li.engine.channelhandler;

import com.li.engine.channelhandler.common.NioNettyFilter;
import com.li.engine.channelhandler.common.impl.ProtocolSelectorHandler;
import com.li.engine.channelhandler.server.AbstractServerVocationalWorkHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.net.ssl.SSLEngine;
import java.util.List;

/**
 * netty 服务端childhandler
 * @author li-yuanwen
 */
@Component
@Slf4j
public class NioNettyServerMessageHandler extends ChannelInitializer<SocketChannel> {

    @Resource
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    @Qualifier("serverSslContext")
    private SslContext sslContext;
    @Resource
    private List<NioNettyFilter> filters;
    @Resource
    private AbstractServerVocationalWorkHandler<?, ?> vocationalWorkHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // SSL 认证
        if (this.sslContext != null) {
            SSLEngine sslEngine = sslContext.newEngine(socketChannel.alloc());
            pipeline.addFirst(SslHandler.class.getSimpleName(), new SslHandler(sslEngine));
        }

        // 过滤器
        if (!CollectionUtils.isEmpty(filters)) {
            for (NioNettyFilter filter : this.filters) {
                pipeline.addLast(filter.getName(), filter);
            }
        }

        // 协议选择
        pipeline.addLast(ProtocolSelectorHandler.class.getSimpleName()
                , applicationContext.getBean(ProtocolSelectorHandler.class));

        // 心跳检测
        IdleStateHandler stateHandler = applicationContext.getBean("serverIdleStateHandler", IdleStateHandler.class);
        pipeline.addLast(IdleStateHandler.class.getSimpleName(), stateHandler);

        // 业务
        pipeline.addLast(AbstractServerVocationalWorkHandler.class.getSimpleName(), this.vocationalWorkHandler);

    }
}
