package com.li.engine.channelhandler.client;

import cn.hutool.core.convert.ConvertException;
import com.li.common.exception.SerializeFailException;
import com.li.common.thread.SerializedExecutorService;
import com.li.engine.service.handler.ThreadLocalContentHolder;
import com.li.engine.service.push.IPushExecutor;
import com.li.engine.service.rpc.SocketFutureManager;
import com.li.engine.service.rpc.future.SocketFuture;
import com.li.network.message.InnerMessage;
import com.li.network.message.PushResponse;
import com.li.network.protocol.*;
import com.li.network.serialize.Serializer;
import com.li.network.serialize.SerializerHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author li-yuanwen
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ClientVocationalWorkHandler extends SimpleChannelInboundHandler<InnerMessage> {

    @Resource
    private SerializerHolder serializerHolder;
    @Resource
    private SocketProtocolManager socketProtocolManager;
    @Resource
    private SocketFutureManager socketFutureManager;
    @Resource
    private IPushExecutor pushExecutor;
    @Resource
    private SerializedExecutorService executorService;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InnerMessage msg) throws Exception {
        // 处理从服务端收到的信息
        if (msg.isRequest()) {
            if (log.isDebugEnabled()) {
                log.debug("客户端收到请求信息,忽略");
            }

            return;
        }

        // 处理收到的推送消息
        if (msg.getProtocol().isPushProtocol()) {
            handlePushMessage(msg);
            return;
        }

        SocketFuture socketFuture = socketFutureManager.removeSocketFuture(msg.getSn());
        if (socketFuture == null) {
            log.warn("客户端收到过期信息,序号[{}],忽略", msg.getSn());
            return;
        }

        long outerSn = socketFuture.getOuterSn();
        long identity = socketFuture.getIdentity();
        if (socketFuture.isSync()) {
            socketFuture.complete(msg);
        } else {
            // 回到玩家业务线程处理
            executorService.submit(identity, () -> {
                ThreadLocalContentHolder.setIdentity(identity);
                ThreadLocalContentHolder.setMessageSn(outerSn);
                try {
                    socketFuture.complete(msg);
                } finally {
                    ThreadLocalContentHolder.removeIdentity();
                    ThreadLocalContentHolder.removeMessageSn();
                }
            });
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
                ctx.channel().writeAndFlush(InnerMessage.HEART_BEAT_REQ);
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    private void handlePushMessage(InnerMessage message) {
        executorService.submit(() -> {
            ProtocolMethodInvokeCtx protocolMethodInvokeCtx = socketProtocolManager.getMethodInvokeCtx(message.getProtocol());
            if (protocolMethodInvokeCtx == null) {
                // 无处理,即仅是中介,直接推送至外网
                Serializer serializer = serializerHolder.getSerializer(message.getSerializeType());
                PushResponse pushResponse = serializer.deserialize(message.getBody(), PushResponse.class);

                pushExecutor.pushToOuter(pushResponse, message.getProtocol());
                return;
            }

            // 查询序列化/反序列化方式
            byte serializeType = message.getSerializeType();
            Serializer serializer = serializerHolder.getSerializer(serializeType);
            if (serializer == null) {
                if (log.isWarnEnabled()) {
                    log.warn("推送消息序列化类型[{}],找不到对应的序列化工具,忽略", serializeType);
                }
                return;
            }

            try {
                // 推送中介逻辑处理
                PushResponse pushResponse = serializer.deserialize(message.getBody(), PushResponse.class);
                ProtocolMethodCtx protocolMethodCtx = protocolMethodInvokeCtx.getProtocolMethodCtx();
                MethodParameter[] params = protocolMethodCtx.getParams();
                Object[] args = new Object[params.length];
                for (int i = 0; i < params.length; i++) {
                    if (params[i] instanceof PushIdsMethodParameter) {
                        args[i] = pushResponse.getContent();
                        continue;
                    }

                    if (params[i] instanceof InBodyMethodParameter) {
                        args[i] = serializer.deserialize(pushResponse.getContent(), params[i].getParameterClass());
                    }
                }

                ReflectionUtils.invokeMethod(protocolMethodCtx.getMethod(), protocolMethodInvokeCtx.getTarget(), args);
            } catch (SerializeFailException e) {
                log.error("发生序列化/反序列化异常", e);
            } catch (ConvertException e) {
                log.error("发生类型转换异常", e);
            } catch (Exception e) {
                log.error("发生未知异常", e);
            }
        });
    }
}
