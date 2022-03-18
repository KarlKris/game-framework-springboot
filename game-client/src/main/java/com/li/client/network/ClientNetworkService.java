package com.li.client.network;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.li.network.message.OuterMessage;
import com.li.network.message.OuterMessageHeader;
import com.li.network.message.ProtocolConstant;
import com.li.network.message.SocketProtocol;
import com.li.network.protocol.SocketProtocolManager;
import com.li.network.serialize.SerializeType;
import com.li.network.serialize.SerializerHolder;
import com.li.protocol.gateway.login.dto.ReqGatewayCreateAccount;
import com.li.protocol.gateway.login.dto.ReqGatewayLoginAccount;
import com.li.protocol.gateway.login.protocol.GatewayLoginModule;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author li-yuanwen
 * @date 2021/12/15
 */
@Slf4j
@Service
public class ClientNetworkService {

    @Resource
    private ClientInitializer clientInitializer;
    @Resource
    private SocketProtocolManager socketProtocolManager;

    private static final String KEY = "GAME-FRAMEWORK-GATEWAY";
    private static final int CHANNEL = 1;
    private static final int SERVER_ID = 1;


    private final AtomicLong snGenerator = new AtomicLong(0);
    private final Map<Long, SocketProtocol> sendSns = new HashMap<>();


    /** 线程组 **/
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

    private Channel channel;

    public void login(String address, int port, String account) throws InterruptedException {
        if (channel != null) {
            return;
        }

        connect(address, port);

        long sn = snGenerator.incrementAndGet();
        SocketProtocol protocol = new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.GAME_SERVER_LOGIN);
        sendSns.put(sn, protocol);

        OuterMessageHeader header = OuterMessageHeader.of(sn, ProtocolConstant.VOCATIONAL_WORK_REQ, protocol, false
                , SerializeType.PROTO_STUFF.getType());

        // body
        int now = DateUtil.thisSecond();
        String sign = SecureUtil.md5(account + now + KEY);
        ReqGatewayLoginAccount gatewayLoginAccount = new ReqGatewayLoginAccount(account, CHANNEL, SERVER_ID, now, sign);

        OuterMessage message = OuterMessage.of(header, SerializerHolder.DEFAULT_SERIALIZER.serialize(gatewayLoginAccount));
        channel.writeAndFlush(message);
    }

    public void create(String address, int port, String account) throws InterruptedException {
        if (channel != null) {
            return;
        }

        connect(address, port);

        long sn = snGenerator.incrementAndGet();
        SocketProtocol protocol = new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.GAME_SERVER_CREATE);
        sendSns.put(sn, protocol);

        OuterMessageHeader header = OuterMessageHeader.of(sn, ProtocolConstant.VOCATIONAL_WORK_REQ, protocol, false
                , SerializeType.PROTO_STUFF.getType());

        // body
        int now = DateUtil.thisSecond();
        String sign = SecureUtil.md5(account + now + KEY);
        ReqGatewayCreateAccount gatewayCreateAccount = new ReqGatewayCreateAccount(account, CHANNEL, SERVER_ID, now, sign);

        OuterMessage message = OuterMessage.of(header, SerializerHolder.DEFAULT_SERIALIZER.serialize(gatewayCreateAccount));
        channel.writeAndFlush(message);
    }


    public void handleResponse(SocketProtocol protocol, byte[] body) {

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
