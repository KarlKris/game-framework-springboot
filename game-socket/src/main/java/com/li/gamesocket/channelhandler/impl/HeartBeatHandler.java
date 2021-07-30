package com.li.gamesocket.channelhandler.impl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * 心跳处理
 */
@Component
@Slf4j
@ChannelHandler.Sharable
@ConditionalOnBean(name = "idleStateHandler")
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    /** 是否开启心跳 **/
    @Value("${netty.server.heartBeat.enable:false}")
    private boolean heartBeatEnable;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 开启心跳,则向对方发送心跳检测包
            if (heartBeatEnable) {
                // 心跳检测包

            }else {
                // 关闭对方连接
                ctx.close();
            }
            return;
        }
        super.userEventTriggered(ctx, evt);
    }
}
