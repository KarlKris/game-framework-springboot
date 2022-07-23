package com.li.engine.codec;

import com.li.network.handler.MessageEncoder;
import com.li.network.message.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * WebSocket 编码器
 * @author li-yuanwen
 * @date 2021/7/29 23:21
 **/
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketEncoder extends ChannelOutboundHandlerAdapter {

    @Resource
    private MessageEncoder messageEncoder;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof IMessage) {
            IMessage message = (IMessage) msg;
            if (log.isDebugEnabled()) {
                log.debug("服务器向WebSocket 写入[{}]协议消息", message.getProtocolHeaderIdentity());
            }

            ByteBuf byteBuf = ctx.alloc().ioBuffer();
            messageEncoder.encode(ctx, message, byteBuf);

            // 转换为二进制帧
            BinaryWebSocketFrame webSocketFrame = new BinaryWebSocketFrame(byteBuf);
            ctx.write(webSocketFrame, promise);
            return;

        }
        super.write(ctx, msg, promise);
    }
}
