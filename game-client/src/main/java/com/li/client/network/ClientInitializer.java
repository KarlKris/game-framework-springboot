package com.li.client.network;

import com.li.network.handler.HeartBeatHandler;
import com.li.network.handler.MessageDecoder;
import com.li.network.handler.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 * @date 2021/12/23
 */
@Slf4j
@Component
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private MessageEncoder messageEncoder;
    @Resource
    private ClientHandler clientHandler;
    @Resource
    private HeartBeatHandler heartBeatHandler;
    @Resource
    private ClientNetworkService clientNetworkService;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 编解码器
        pipeline.addLast(MessageEncoder.class.getSimpleName(), this.messageEncoder);
        pipeline.addLast(MessageDecoder.class.getSimpleName(), new MessageDecoder(1048756, 2, 4));

        // 心跳
        pipeline.addLast(IdleStateHandler.class.getSimpleName()
                , new IdleStateHandler(0, 25, 0, TimeUnit.SECONDS));
        pipeline.addLast(HeartBeatHandler.class.getSimpleName(), this.heartBeatHandler);

        // 业务
        pipeline.addLast(ClientNetworkService.class.getSimpleName(), this.clientNetworkService);

    }
}
