package com.li.gamesocket.channelhandler;

import com.li.gamesocket.channelhandler.impl.ProtocolSelectorHandler;
import com.li.gamesocket.ssl.SslConfig;
import com.li.gamesocket.ssl.SslContextFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author li-yuanwen
 * netty 服务端childhandler
 */
@Component
public class NioNettyServerMessageHandler extends ChannelInitializer<SocketChannel> {

    @Autowired
    private SslConfig sslConfig;
    @Autowired
    private List<NioNettyFilter> filters;
    @Autowired
    private ProtocolSelectorHandler protocolSelectorHandler;
    @Autowired
    private IdleStateHandler idleStateHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // SSL 认证
        if (this.sslConfig.isSllEnable()) {
            pipeline.addFirst(SslHandler.class.getSimpleName(), new SslHandler(SslContextFactory.getSslEngine(this.sslConfig)));
        }

        // 过滤器
        if (!CollectionUtils.isEmpty(filters)) {
            for (NioNettyFilter filter : this.filters) {
                pipeline.addLast(filter.getName(), filter);
            }
        }

        // 协议选择
        pipeline.addLast(ProtocolSelectorHandler.class.getSimpleName(), this.protocolSelectorHandler);

        // 心跳检测
        pipeline.addLast(IdleStateHandler.class.getSimpleName(), this.idleStateHandler);

        // 业务


    }
}
