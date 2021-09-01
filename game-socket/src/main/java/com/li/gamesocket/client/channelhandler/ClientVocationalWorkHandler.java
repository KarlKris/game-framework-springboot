package com.li.gamesocket.client.channelhandler;

import cn.hutool.core.util.ZipUtil;
import com.li.gamecommon.exception.BadRequestException;
import com.li.gamecommon.exception.SocketException;
import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.protocol.*;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerManager;
import com.li.gamesocket.service.VocationalWorkConfig;
import com.li.gamesocket.service.rpc.ForwardSnCtx;
import com.li.gamesocket.service.rpc.RpcSnCtx;
import com.li.gamesocket.service.rpc.SnCtx;
import com.li.gamesocket.service.rpc.SnCtxManager;
import com.li.gamesocket.service.session.Session;
import com.li.gamesocket.service.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author li-yuanwen
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class ClientVocationalWorkHandler extends SimpleChannelInboundHandler<IMessage> {

    @Autowired
    private SnCtxManager snCtxManager;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private SerializerManager serializerManager;
    @Autowired
    private VocationalWorkConfig vocationalWorkConfig;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMessage msg) throws Exception {
        // 处理从服务端收到的信息
        if (msg.isRequest()) {
            if (log.isDebugEnabled()) {
                log.debug("客户端ClientVocationalWorkHandler收到请求信息,忽略");
            }

            return;
        }

        // 处理从服务端收到的推送消息
        if (msg.getCommand().push()) {

            Serializer serializer = serializerManager.getSerializer(msg.getSerializeType());
            PushResponse pushResponse = serializer.deserialize(msg.getBody(), PushResponse.class);

            Response response = Response.SUCCESS(pushResponse.getContent());

            Byte serializeType = null;
            byte[] body = null;
            boolean zip = false;

            for (long identity : pushResponse.getTargets()) {
                Session session = sessionManager.getIdentitySession(identity);
                if (session == null) {
                    continue;
                }

                Byte type = session.getChannel().attr(ChannelAttributeKeys.LAST_SERIALIZE_TYPE).get();
                if (!Objects.equals(type, serializeType)) {
                    serializeType = type;
                    serializer = serializerManager.getSerializer(type);
                    body = serializer.serialize(response);
                    if (body.length > vocationalWorkConfig.getBodyZipLength()) {
                        body = ZipUtil.gzip(body);
                        zip = true;
                    }
                }

                if (log.isDebugEnabled()) {
                    log.debug("向玩家[{}]推送消息", identity);
                }

                Short lastProtocolHeaderIdentity = session.getChannel().attr(ChannelAttributeKeys.LAST_PROTOCOL_HEADER_IDENTITY).get();
                if (lastProtocolHeaderIdentity == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("未知玩家[{}]Channel使用的消息类型,忽略本次推送", identity);
                    }
                    continue;
                }

                IMessage message = null;
                if (lastProtocolHeaderIdentity == ProtocolConstant.PROTOCOL_INNER_HEADER_IDENTITY) {
                    // 内部通信类型
                    message = MessageFactory.toInnerMessage(msg.getSn()
                            , ProtocolConstant.toOriginMessageType(msg.getMessageType())
                            , msg.getCommand()
                            , serializeType
                            , zip
                            , body
                            , session);
                }else if(lastProtocolHeaderIdentity == ProtocolConstant.PROTOCOL_OUTER_HEADER_IDENTITY) {
                    // 外部通信类型
                    message = MessageFactory.toOuterMessage(msg.getSn()
                            , ProtocolConstant.toOriginMessageType(msg.getMessageType())
                            , msg.getCommand()
                            , serializeType
                            , zip
                            , body);
                }

                sessionManager.writeAndFlush(session, message);
            }

            return;
        }

        SnCtx snCtx = this.snCtxManager.remove(msg.getSn());
        if (snCtx == null) {
            if (log.isDebugEnabled()) {
                log.debug("客户端ClientVocationalWorkHandler收到过期信息,序号[{}],忽略", msg.getSn());
            }

            return;
        }

        // 直接转发给源目标
        if (snCtx instanceof ForwardSnCtx) {

            ForwardSnCtx forwardSnCtx = (ForwardSnCtx) snCtx;

            Channel channel = forwardSnCtx.getChannel();
            Session session = channel.attr(ChannelAttributeKeys.SESSION).get();

            if (log.isDebugEnabled()) {
                log.debug("转发响应消息[{}]至[{}]", msg, session.ip());
            }

            IMessage message = null;
            short protocolHeaderIdentity = msg.getProtocolHeaderIdentity();
            if (protocolHeaderIdentity == ProtocolConstant.PROTOCOL_OUTER_HEADER_IDENTITY) {
                message = MessageFactory.toOuterMessage(forwardSnCtx.getSn()
                        , ProtocolConstant.toOriginMessageType(msg.getMessageType())
                        , msg.getCommand()
                        , msg.getSerializeType()
                        , msg.zip()
                        , msg.getBody());
            }else if (protocolHeaderIdentity == ProtocolConstant.PROTOCOL_INNER_HEADER_IDENTITY) {
                message = MessageFactory.toInnerMessage(forwardSnCtx.getSn()
                        , ProtocolConstant.toOriginMessageType(msg.getMessageType())
                        , msg.getCommand()
                        , msg.getSerializeType()
                        , msg.zip()
                        , msg.getBody()
                        , session);
            }

            sessionManager.writeAndFlush(session, message);

            return;
        }

        if (snCtx instanceof RpcSnCtx) {

            RpcSnCtx rpcSnCtx = (RpcSnCtx) snCtx;

            CompletableFuture<Response> future = rpcSnCtx.getFuture();

            Serializer serializer = serializerManager.getSerializer(msg.getSerializeType());
            Response response = serializer.deserialize(msg.getBody(), Response.class);
            if (response.success()) {
                future.complete(response);
            } else {
                if (response.isVocationalException()) {
                    future.completeExceptionally(new BadRequestException(response.getCode(), "请求远程服务业务异常"));
                } else {
                    future.completeExceptionally(new SocketException(response.getCode(), "请求远程服务通讯异常"));
                }
            }

            return;
        }

        if (log.isWarnEnabled()) {
            log.warn("客户端ClientVocationalWorkHandler收到信息[{}],但不处理", snCtx);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.error("客户端发生IOException,与服务端断开连接", cause);
            ctx.close();
        } else {
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
