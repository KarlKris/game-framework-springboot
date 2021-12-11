package com.li.gamesocket.channelhandler.server;

import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.service.handler.Dispatcher;
import com.li.gamesocket.service.session.ISession;
import com.li.gamesocket.service.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 抽象服务端的业务逻辑处理ChannelHandler基类
 * @author li-yuanwen
 * @date 2021/12/8
 */
@Slf4j
public abstract class AbstractServerVocationalWorkHandler<M extends IMessage, S extends ISession> extends SimpleChannelInboundHandler<M> {

    @Resource
    protected SessionManager sessionManager;
    @Resource
    protected Dispatcher<M, S> dispatcher;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.error("服务器发生IOException,与客户端[{}]断开连接", ctx.channel().id(), cause);
        }else {
            log.error("服务器发生未知异常", cause);
        }
        ctx.close();
    }
}
