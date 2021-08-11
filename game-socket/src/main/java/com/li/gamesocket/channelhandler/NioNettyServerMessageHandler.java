package com.li.gamesocket.channelhandler;

import com.li.gamecommon.ApplicationContextHolder;
import com.li.gamesocket.channelhandler.impl.ProtocolSelectorHandler;
import com.li.gamesocket.channelhandler.impl.VocationalWorkHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLEngine;
import java.util.List;

/**
 * @author li-yuanwen
 * netty 服务端childhandler
 */
@Component
public class NioNettyServerMessageHandler extends ChannelInitializer<SocketChannel> {

    @Autowired(required = false)
    @Qualifier("serverSslContext")
    private SslContext sslContext;
    @Autowired
    private List<NioNettyFilter> filters;
    @Autowired
    private VocationalWorkHandler vocationalWorkHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // SSL 认证
        if (this.sslContext != null) {
            SSLEngine sslEngine = this.sslContext.newEngine(socketChannel.alloc());
            sslEngine.setUseClientMode(false);
            pipeline.addFirst(SslHandler.class.getSimpleName(), new SslHandler(sslEngine));
        }

        // 过滤器
        if (!CollectionUtils.isEmpty(filters)) {
            for (NioNettyFilter filter : this.filters) {
                pipeline.addLast(filter.getName(), filter);
            }
        }

        // 协议选择
        pipeline.addLast(ProtocolSelectorHandler.class.getSimpleName(), ApplicationContextHolder.getBean(ProtocolSelectorHandler.class));

        // 心跳检测
        pipeline.addLast(IdleStateHandler.class.getSimpleName()
                , ApplicationContextHolder.getBean("serverIdleStateHandler", IdleStateHandler.class));

        // 业务
        pipeline.addLast(VocationalWorkHandler.class.getSimpleName(), this.vocationalWorkHandler);

    }
}
