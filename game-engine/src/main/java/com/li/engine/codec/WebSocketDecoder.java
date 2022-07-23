package com.li.engine.codec;

import com.li.network.handler.MessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * WebSocket 消息解码器
 * @author li-yuanwen
 * @date 2021/7/29 23:06
 **/
@Component
@Slf4j
@ChannelHandler.Sharable
public class WebSocketDecoder extends ChannelInboundHandlerAdapter {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
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

            ByteBuf byteBuf = ((WebSocketFrame) msg).content();
            applicationContext.getBean(MessageDecoder.class).channelRead(ctx, byteBuf);
        }else {
            super.channelRead(ctx, msg);
        }


    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            if (log.isDebugEnabled()) {
                log.info("websocket 握手成功。");
            }
            WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            if (log.isDebugEnabled()) {
                handshakeComplete.requestHeaders().forEach(entry -> log.info("header key:[{}] value:[{}]", entry.getKey(), entry.getValue()));
            }

        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
