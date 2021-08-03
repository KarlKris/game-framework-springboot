package com.li.gamesocket.client.channelhandler;

import com.li.gamesocket.codec.MessageDecoder;
import com.li.gamesocket.codec.MessageEncoder;
import com.li.gamesocket.ssl.SslConfig;
import com.li.gamesocket.ssl.SslContextFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 */
@Component
@Slf4j
public class NioNettyClientMessageHandler extends ChannelInitializer<SocketChannel> {

    @Autowired
    private SslConfig sslConfig;
    @Autowired
    private MessageEncoder messageEncoder;
    @Autowired
    private MessageDecoder messageDecoder;
    @Autowired(required = false)
    @Qualifier("clientIdleStateHandler")
    private IdleStateHandler idleStateHandler;
    @Autowired
    private ClientVocationalWorkHandler clientVocationalWorkHandler;


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (this.sslConfig.isSllEnable()) {
            pipeline.addFirst(SslHandler.class.getSimpleName()
                    , new SslHandler(SslContextFactory.getSslEngine(this.sslConfig)));
        }

        // 编解码器
        pipeline.addLast(MessageEncoder.class.getSimpleName(), this.messageEncoder);
        pipeline.addLast(MessageDecoder.class.getSimpleName(), this.messageDecoder);

        // 心跳
        if (this.idleStateHandler != null) {
            pipeline.addLast(IdleStateHandler.class.getSimpleName(), this.idleStateHandler);
        }

        // 业务
        pipeline.addLast(ClientVocationalWorkHandler.class.getSimpleName(), this.clientVocationalWorkHandler);

    }
}
