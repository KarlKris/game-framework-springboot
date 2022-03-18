package com.li.client.network;

import com.li.network.message.OuterMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 消息处理
 * @author li-yuanwen
 * @date 2021/12/15
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<OuterMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, OuterMessage outerMessage) throws Exception {

    }
}
