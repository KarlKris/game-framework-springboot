package com.li.gamesocket.client.channelhandler;

import com.li.gamecommon.ApplicationContextHolder;
import com.li.gamesocket.codec.MessageDecoder;
import com.li.gamesocket.codec.MessageEncoder;
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
    @Autowired
    private MessageEncoder messageEncoder;
    @Autowired
    private ClientVocationalWorkHandler clientVocationalWorkHandler;


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

        // 业务
        pipeline.addLast(ClientVocationalWorkHandler.class.getSimpleName(), this.clientVocationalWorkHandler);

    }
}
