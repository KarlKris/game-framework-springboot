package com.li.gamesocket.channelhandler.impl;

import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.protocol.MessageFactory;
import com.li.gamesocket.protocol.ProtocolConstant;
import com.sun.org.apache.regexp.internal.RE;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
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
public class HeartBeatHandler extends SimpleChannelInboundHandler<IMessage> {

    /** 是否开启心跳 **/
    @Value("${netty.server.heartBeat.enable:false}")
    private boolean heartBeatEnable;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMessage msg) throws Exception {
        if (msg.isHeartBeatRequest()) {
            if (msg.isInnerMessage()) {
                // 发生心跳响应包
                ctx.channel().writeAndFlush(MessageFactory.HEART_BEAT_RES_INNER_MSG);
                return;
            }

            if (msg.isOuterMessage()) {
                // 发生心跳响应包
                ctx.channel().writeAndFlush(MessageFactory.HEART_BEAT_RES_OUTER_MSG);
                return;
            }

            if (log.isWarnEnabled()) {
                log.warn("收到协议头[{}],暂不支持进行心跳响应,忽略", msg.getProtocolHeaderIdentity());
            }

            return;
        }

        if (log.isDebugEnabled() && msg.isHeartBeatResponse()) {
            log.debug("收到心跳信息响应包,忽略");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 开启心跳,则向对方发送心跳检测包
            if (heartBeatEnable) {
                Short protocolHeaderIdentity = ctx.channel().attr(ChannelAttributeKeys.LAST_PROTOCOL_HEADER_IDENTITY).get();
                if (protocolHeaderIdentity == null) {
                    // 未正常通信过,忽略
                    return;
                }
                if (protocolHeaderIdentity == ProtocolConstant.PROTOCOL_INNER_HEADER_IDENTITY) {
                    // 发生心跳检测包
                    ctx.channel().writeAndFlush(MessageFactory.HEART_BEAT_REQ_INNER_MSG);
                    return;
                }

                if (protocolHeaderIdentity == ProtocolConstant.PROTOCOL_OUTER_HEADER_IDENTITY) {
                    // 发生心跳检测包
                    ctx.channel().writeAndFlush(MessageFactory.HEART_BEAT_RES_OUTER_MSG);
                    return;
                }

                if (log.isWarnEnabled()) {
                    log.warn("收到协议头[{}],暂不支持进行心跳检测,断开连接", protocolHeaderIdentity);
                }

                // 关闭连接
                ctx.close();

            }else {
                // 关闭对方连接
                ctx.close();
            }
            return;
        }
        super.userEventTriggered(ctx, evt);
    }
}
