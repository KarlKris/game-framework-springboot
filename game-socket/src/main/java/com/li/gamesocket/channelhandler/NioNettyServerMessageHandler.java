package com.li.gamesocket.channelhandler;

import com.li.gamesocket.ssl.SslConfig;
import com.li.gamesocket.ssl.SslContextFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;
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

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // SSL 认证
        if (this.sslConfig.isSllEnable()) {
            pipeline.addFirst("SSL_HANDLER", new SslHandler(SslContextFactory.getSslEngine(this.sslConfig)));
        }

        // 过滤器
        if (!CollectionUtils.isEmpty(filters)) {
            for (NioNettyFilter filter : this.filters) {
                pipeline.addLast(filter.getName(), filter);
            }
        }

        // 协议选择


        // 心跳检测



    }
}
