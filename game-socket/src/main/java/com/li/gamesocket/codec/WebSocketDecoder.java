package com.li.gamesocket.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * @date 2021/7/29 23:06
 * WebSocket 消息解码器
 **/
@Component
@Slf4j
@ChannelHandler.Sharable
public class WebSocketDecoder extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Autowired
    private MessageDecoder messageDecoder;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        // ping pong 忽略
        if (msg instanceof PingWebSocketFrame || msg instanceof PongWebSocketFrame) {
            if (log.isDebugEnabled()) {
                log.debug("服务器收到了WebSocket Ping/Pong帧,忽略");
            }
            return;
        }

        // 关闭
        if (msg instanceof CloseWebSocketFrame) {
            if (log.isDebugEnabled()) {
                log.debug("服务器收到了WebSocket CloseWebSocketFrame 准备关闭连接");
            }
            ctx.close();
            return;
        }

        ByteBuf byteBuf = msg.content();
        messageDecoder.channelRead(ctx, byteBuf);
    }
}
