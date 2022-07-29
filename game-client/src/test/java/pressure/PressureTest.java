package pressure;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.crypto.SecureUtil;
import com.li.network.handler.MessageDecoder;
import com.li.network.handler.MessageEncoder;
import com.li.network.message.OuterMessage;
import com.li.network.message.OuterMessageHeader;
import com.li.network.message.ProtocolConstant;
import com.li.network.message.SocketProtocol;
import com.li.network.serialize.Serializer;
import com.li.network.serialize.impl.ProtoStuffSerializer;
import com.li.protocol.game.chat.protocol.ChatModule;
import com.li.protocol.gateway.login.protocol.GatewayLoginModule;
import com.li.protocol.gateway.login.vo.ReqGatewayLoginAccount;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

/**
 * 压力测试
 * @author li-yuanwen
 * @date 2022/7/27
 */
public class PressureTest {

    private static final String KEY = "GAME-FRAMEWORK-GATEWAY";
    private static final int CHANNEL = 1;
    private static final int SERVER_ID = 1;

    private static final Serializer DEFAULT_SERIALIZER = new ProtoStuffSerializer();

    private void initAndConnect(ChannelInboundHandler handler) throws InterruptedException {

        final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1, new NamedThreadFactory("test-", false));

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        // 编解码器
                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(new MessageDecoder(1048756, 2, 4));

                        // 业务
                        pipeline.addLast(handler);
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8088).sync();
        channelFuture.channel().closeFuture().addListener(future -> eventLoopGroup.shutdownGracefully());

    }

    @Test
    public void WriteBufferWaterMarkTest() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        ChannelInboundHandlerAdapter adapter = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OuterMessageHeader header = OuterMessageHeader.of(1, ProtocolConstant.VOCATIONAL_WORK_REQ
                                , new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.LOGIN_ACCOUNT), false
                                , DEFAULT_SERIALIZER.getSerializerType());

                        String account = "test";
                        int now = DateUtil.thisSecond();
                        String sign = SecureUtil.md5(account + now + KEY);

                        ReqGatewayLoginAccount gatewayLoginAccount = new ReqGatewayLoginAccount(account, CHANNEL, SERVER_ID, now, sign);
                        byte[] requestBody = DEFAULT_SERIALIZER.serialize(gatewayLoginAccount);
                        OuterMessage message = OuterMessage.of(header, requestBody);
                        ctx.writeAndFlush(message);

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        header = OuterMessageHeader.of(1, ProtocolConstant.VOCATIONAL_WORK_REQ
                                , new SocketProtocol(ChatModule.MODULE, ChatModule.SEND), false
                                , DEFAULT_SERIALIZER.getSerializerType());
                        requestBody = DEFAULT_SERIALIZER.serialize("hello");
                        message = OuterMessage.of(header, requestBody);
                        while (true) {
                            if (ctx.channel().isWritable()) {
                                ctx.writeAndFlush(message);
                            } else {
                                System.out.println("待发送队列:Buffer.size => " + ctx.channel().unsafe().outboundBuffer().nioBufferSize());
                                ctx.close();
                                break;
                            }
                        }
                        latch.countDown();
                    }
                });
                thread.start();
            }
        };
        initAndConnect(adapter);
        latch.await();
    }


}
