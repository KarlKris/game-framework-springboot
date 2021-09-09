package com.li.gamegateway;

import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.gamesocket.codec.MessageDecoder;
import com.li.gamesocket.codec.MessageEncoder;
import com.li.gamesocket.protocol.OuterMessage;
import com.li.gamesocket.protocol.OuterMessageHeader;
import com.li.gamesocket.protocol.ProtocolConstant;
import com.li.gamesocket.protocol.Request;
import com.li.gamesocket.protocol.serialize.SerializeType;
import com.li.gamesocket.service.command.Command;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author li-yuanwen
 * 网关服基准测试
 */
@BenchmarkMode(Mode.Throughput) // 吞吐量
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 4) // 先预热4轮
@Measurement(iterations = 10) // 进行10轮测试
public class GatewayBenchmark {

    private Map<Integer, Channel> clients = new HashMap<>(5000);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private AtomicInteger index = new AtomicInteger(0);
    private int total = 5000;


    @Setup(Level.Trial)
    public void init() throws JsonProcessingException, InterruptedException {
        Command command = new Command((short)1, (byte)2);
        OuterMessageHeader header = OuterMessageHeader.of(0L, ProtocolConstant.VOCATIONAL_WORK_REQ, command, false, SerializeType.JSON.getType());

        int timestamp = 1628676236;

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(8);
        for (int i = 0; i < total; i++) {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(MessageEncoder.class.getSimpleName(), new MessageEncoder());
                            pipeline.addLast(MessageDecoder.class.getSimpleName(), new MessageDecoder(1024*1024, 2, 4));

                        }
                    });

            ChannelFuture future = bootstrap.connect("192.168.11.65", 8088).sync();
            Channel channel = future.channel();

            Map<String, Object> map = new HashMap<>(5);
            String account = "test_" + i;

            map.put("account", account);
            map.put("channel", 1);
            map.put("serverId", 10);
            map.put("timestamp", timestamp);
            map.put("sign", SecureUtil.md5(account + timestamp + "GAME-FRAMEWORK-GATEWAY"));

            OuterMessage message = OuterMessage.of(header, objectMapper.writeValueAsBytes(new Request(map)));

            channel.writeAndFlush(message);
            clients.put(i, channel);
        }


    }

    @Benchmark
    public void chat() throws JsonProcessingException {
        Channel client = clients.get(index.getAndIncrement() % total);
        Command command = new Command((short)3, (byte)1);
        OuterMessageHeader header = OuterMessageHeader.of(0L, ProtocolConstant.VOCATIONAL_WORK_REQ, command, false, SerializeType.JSON.getType());
        OuterMessage message = OuterMessage.of(header, objectMapper.writeValueAsBytes(new Request(Collections.singletonMap("msg", "hello"))));
        client.writeAndFlush(message);
    }


    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(GatewayBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }


}
