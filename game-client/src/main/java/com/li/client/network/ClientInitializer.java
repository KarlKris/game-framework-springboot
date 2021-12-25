package com.li.client.network;

import com.li.network.handler.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

    }
}
