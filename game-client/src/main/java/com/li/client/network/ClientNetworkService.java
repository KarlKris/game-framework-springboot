package com.li.client.network;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.client.controller.MessageController;
import com.li.client.handler.ProtocolResponseBodyHandler;
import com.li.client.stat.EfficiencyStatistic;
import com.li.network.message.*;
import com.li.network.modules.ErrorCodeModule;
import com.li.network.protocol.InBodyMethodParameter;
import com.li.network.protocol.MethodParameter;
import com.li.network.protocol.ProtocolMethodCtx;
import com.li.network.protocol.SocketProtocolManager;
import com.li.network.serialize.Serializer;
import com.li.network.serialize.SerializerHolder;
import com.li.protocol.gateway.login.protocol.GatewayLoginModule;
import com.li.protocol.gateway.login.vo.ReqGatewayCreateAccount;
import com.li.protocol.gateway.login.vo.ReqGatewayLoginAccount;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author li-yuanwen
 * @date 2021/12/15
 */
@Slf4j
@Service
@ChannelHandler.Sharable
public class ClientNetworkService extends SimpleChannelInboundHandler<IMessage> {

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private ClientInitializer clientInitializer;
    @Resource
    private EfficiencyStatistic efficiencyStatistic;
    @Resource
    private SocketProtocolManager socketProtocolManager;
    @Resource
    private SerializerHolder serializerHolder;
    @Resource
    protected MessageController messageController;
    @Resource
    private ObjectMapper objectMapper;

    private final Map<SocketProtocol, ProtocolResponseBodyHandler<?>> responseBodyHandlerHolder = new HashMap<>();

    @PostConstruct
    private void init() {
        for (ProtocolResponseBodyHandler<?> handler : applicationContext.getBeansOfType(ProtocolResponseBodyHandler.class).values()) {
            for (SocketProtocol protocol : handler.getSocketProtocol()) {
                ProtocolResponseBodyHandler<?> old = responseBodyHandlerHolder.putIfAbsent(protocol, handler);
                if (old != null) {
                    throw new BeanInitializationException("协议:" + protocol + "响应体处理器重复");
                }
            }

        }
    }


    private static final String KEY = "GAME-FRAMEWORK-GATEWAY";
    private static final int CHANNEL = 1;
    private static final int SERVER_ID = 1;

    /** 线程组 **/
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

    private Channel channel;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IMessage msg) throws Exception {
        // 理论上消息都是内部消息
        if (msg instanceof InnerMessage) {
            log.warn("客户端收到内部消息InnerMessage[{}],理论上消息都是外部消息,请检查逻辑", msg);
            return;
        }

        SocketProtocol protocol = msg.getProtocol();
        OuterMessage message = (OuterMessage) msg;
        byte[] body = message.getBody();
        Object responseBody = null;
        if (ArrayUtil.isNotEmpty(body)) {
            if (message.isZip()) {
                body = ZipUtil.unGzip(body);
            }

            Class<?> returnClz = null;
            if (protocol.equals(ErrorCodeModule.ERROR_CODE_RESPONSE)) {
                returnClz = Long.class;
            } else {
                ProtocolMethodCtx protocolMethodCtx = socketProtocolManager.getMethodCtxBySocketProtocol(protocol);
                if (protocol.isPushProtocol()) {
                    for (MethodParameter param : protocolMethodCtx.getParams()) {
                        if (param instanceof InBodyMethodParameter) {
                            returnClz = ((InBodyMethodParameter) param).getParameterClass();
                            break;
                        }
                    }
                } else {
                    returnClz = protocolMethodCtx.getReturnClz();
                }
            }

            Serializer serializer = serializerHolder.getSerializer(message.getSerializeType());
            responseBody = serializer.deserialize(body, returnClz);
        }

        if (!protocol.isPushProtocol()) {
            EfficiencyStatistic.SingleProtocolStat singleProtocolStat = efficiencyStatistic.finishSingleProtocol(msg.getSn(), protocol);
            ProtocolResponseBodyHandler bodyHandler = responseBodyHandlerHolder.get(singleProtocolStat.getProtocol());
            if (bodyHandler != null) {
                bodyHandler.handle(singleProtocolStat.getProtocol(), protocol, responseBody);
            } else {
                if (protocol.equals(singleProtocolStat.getProtocol())) {
                    messageController.addInfoMessage(objectMapper.writeValueAsString(responseBody));
                } else {
                    messageController.addErrorMessage(objectMapper.writeValueAsString(responseBody));
                }
            }
        } else {
            ProtocolResponseBodyHandler bodyHandler = responseBodyHandlerHolder.get(protocol);
            if (bodyHandler != null) {
                bodyHandler.handle(protocol, protocol, responseBody);
            } else {
                messageController.addInfoMessage("收到推送: " + objectMapper.writeValueAsString(responseBody));
            }
        }

    }

    public void send(SocketProtocol protocol, Object body) {
        long sn = efficiencyStatistic.nextSn();
        OuterMessageHeader header = OuterMessageHeader.of(sn, ProtocolConstant.VOCATIONAL_WORK_REQ, protocol, false
                , SerializerHolder.DEFAULT_SERIALIZER.getSerializerType());


        byte[] requestBody = null;
        if (body != null) {
            requestBody = SerializerHolder.DEFAULT_SERIALIZER.serialize(body);
        }

        OuterMessage message = OuterMessage.of(header, requestBody);
        efficiencyStatistic.requestSingleProtocol(sn, protocol);
        channel.writeAndFlush(message);

    }

    public void login(String address, int port, String account) throws InterruptedException {
        if (channel == null || !channel.isActive()) {
            connect(address, port);
        }

        SocketProtocol protocol = new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.LOGIN_ACCOUNT);

        // body
        int now = DateUtil.thisSecond();
        String sign = SecureUtil.md5(account + now + KEY);
        ReqGatewayLoginAccount gatewayLoginAccount = new ReqGatewayLoginAccount(account, CHANNEL, SERVER_ID, now, sign);

        send(protocol, gatewayLoginAccount);
    }

    public void create(String address, int port, String account) throws InterruptedException {
        if (channel == null || !channel.isActive()) {
            connect(address, port);
        }

        SocketProtocol protocol = new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.CREATE_ACCOUNT);

        // body
        int now = DateUtil.thisSecond();
        String sign = SecureUtil.md5(account + now + KEY);
        ReqGatewayCreateAccount gatewayCreateAccount = new ReqGatewayCreateAccount(account, CHANNEL, SERVER_ID, now, sign);

        send(protocol, gatewayCreateAccount);

    }


    private void connect(String address, int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(clientInitializer);

        ChannelFuture future = bootstrap.connect(address, port).sync();
        this.channel = future.channel();

        log.warn("客户端连接[{}:{}]成功", address, port);
    }

}
