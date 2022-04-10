package com.li.engine.channelhandler;

import com.li.common.ApplicationContextHolder;
import com.li.engine.channelhandler.client.ClientVocationalWorkHandler;
import com.li.engine.channelhandler.common.HeartBeatHandler;
import com.li.network.handler.MessageDecoder;
import com.li.network.handler.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.net.ssl.SSLEngine;

/**
 * @author li-yuanwen
 */
@Component
@Slf4j
public class NioNettyClientMessageHandler extends ChannelInitializer<SocketChannel> {

    @Autowired(required = false)
    @Qualifier("clientSslContext")
    private SslContext sslContext;
    @Resource
    private MessageEncoder messageEncoder;
    @Resource
    private ClientVocationalWorkHandler clientVocationalWorkHandler;
    @Resource
    private HeartBeatHandler heartBeatHandler;


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (this.sslContext != null) {
            SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
            sslEngine.setUseClientMode(true);
            pipeline.addFirst(SslHandler.class.getSimpleName()
                    , new SslHandler(sslEngine));
        }

        // 编解码器
        pipeline.addLast(MessageEncoder.class.getSimpleName(), this.messageEncoder);
        pipeline.addLast(MessageDecoder.class.getSimpleName(), ApplicationContextHolder.getBean(MessageDecoder.class));

        // 心跳
        pipeline.addLast(IdleStateHandler.class.getSimpleName()
                , ApplicationContextHolder.getBean("clientIdleStateHandler", IdleStateHandler.class));
        pipeline.addLast(HeartBeatHandler.class.getSimpleName(), this.heartBeatHandler);

        // 业务
        pipeline.addLast(ClientVocationalWorkHandler.class.getSimpleName(), this.clientVocationalWorkHandler);

    }
}
