package com.li.gamesocket.codec;

import com.li.gamecore.ApplicationContextHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
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
        ApplicationContextHolder.getBean(MessageDecoder.class).channelRead(ctx, byteBuf);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            if (log.isDebugEnabled()) {
                log.info("websocket 握手成功。");
            }
            WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String requestUri = handshakeComplete.requestUri();
            if (log.isDebugEnabled()) {
                log.info("requestUri:[{}]", requestUri);
            }
            String subproTocol = handshakeComplete.selectedSubprotocol();
            if (log.isDebugEnabled()) {
                log.info("subproTocol:[{}]", subproTocol);
                handshakeComplete.requestHeaders().forEach(entry -> log.info("header key:[{}] value:[{}]", entry.getKey(), entry.getValue()));
            }

        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
