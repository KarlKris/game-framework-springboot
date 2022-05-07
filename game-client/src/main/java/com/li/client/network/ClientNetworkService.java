package com.li.client.network;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.li.client.stat.EfficiencyStatistic;
import com.li.network.message.*;
import com.li.network.serialize.SerializerHolder;
import com.li.protocol.gateway.login.dto.ReqGatewayCreateAccount;
import com.li.protocol.gateway.login.dto.ReqGatewayLoginAccount;
import com.li.protocol.gateway.login.protocol.GatewayLoginModule;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author li-yuanwen
 * @date 2021/12/15
 */
@Slf4j
@Service
@ChannelHandler.Sharable
public class ClientNetworkService extends SimpleChannelInboundHandler<IMessage> {

    @Resource
    private ClientInitializer clientInitializer;
    @Resource
    private EfficiencyStatistic efficiencyStatistic;


    private static final String KEY = "GAME-FRAMEWORK-GATEWAY";
    private static final int CHANNEL = 1;
    private static final int SERVER_ID = 1;


    private final AtomicLong snGenerator = new AtomicLong(0);
    private final Set<Long> sendSns = new HashSet<>(16);


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

        OuterMessage message = (OuterMessage) msg;


    }


    private void send(SocketProtocol protocol, Object body) {
        long sn = snGenerator.incrementAndGet();
        sendSns.add(sn);
        OuterMessageHeader header = OuterMessageHeader.of(sn, ProtocolConstant.VOCATIONAL_WORK_REQ, protocol, false
                , SerializerHolder.DEFAULT_SERIALIZER.getSerializerType());
        OuterMessage message = OuterMessage.of(header, SerializerHolder.DEFAULT_SERIALIZER.serialize(body));
        channel.writeAndFlush(message);

    }

    public void login(String address, int port, String account) throws InterruptedException {
        if (channel != null) {
            return;
        }

        connect(address, port);

        SocketProtocol protocol = new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.GAME_SERVER_LOGIN);

        // body
        int now = DateUtil.thisSecond();
        String sign = SecureUtil.md5(account + now + KEY);
        ReqGatewayLoginAccount gatewayLoginAccount = new ReqGatewayLoginAccount(account, CHANNEL, SERVER_ID, now, sign);

        send(protocol, gatewayLoginAccount);
    }

    public void create(String address, int port, String account) throws InterruptedException {
        if (channel != null) {
            return;
        }

        connect(address, port);

        SocketProtocol protocol = new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.GAME_SERVER_CREATE);

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
