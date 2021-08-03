package com.li.gamesocket.client.channelhandler;

import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.protocol.MessageFactory;
import com.li.gamesocket.service.SerialNumberManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author li-yuanwen
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class ClientVocationalWorkHandler extends SimpleChannelInboundHandler<IMessage> {

    @Autowired
    private SerialNumberManager serialNumberManager;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMessage msg) throws Exception {
        // todo 处理从服务端收到的信息
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.error("客户端发生IOException,与服务端断开连接", cause);
            ctx.close();
        }else {
            log.error("客户端发生未知异常", cause);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            // 开启心跳,则向对方发送心跳检测包
            if (event.state() == IdleState.WRITER_IDLE) {
                // 发生心跳检测包
                ctx.channel().writeAndFlush(MessageFactory.HEART_BEAT_REQ_INNER_MSG);
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
