package com.li.gamesocket.channelhandler.config;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 * @date 2021/7/29 21:56
 * channelHandler注解方式配置Bean
 **/
@Configuration
public class ChannelHandlerConfiguration {


    /** IdleStateHandler 心跳检测多长时间(秒)没读到数据时抛出IdleStateEvent.READER_IDLE事件 **/
    @Value("${netty.server.idle.read.seconds:5}")
    private int readTime;

    /** IdleStateHandler 心跳检测多长时间(秒)没向对方发送数据时抛出IdleStateEvent.WRITER_IDLE事件 **/
    @Value("${netty.server.idle.write.seconds:0}")
    private int writeTime;

    /** IdleStateHandler 心跳检测多长时间(秒)没任何操作时抛出IdleStateEvent.ALL_IDLE事件 **/
    @Value("${netty.server.idle.all.seconds:5}")
    private int allTime;


    /** 心跳检测ChannelHandler **/
    @Bean
    @ConditionalOnExpression("${netty.server.idle.read.seconds:5}>0 " +
            "|| ${netty.server.idle.write.seconds:0}>0 " +
            "|| ${netty.server.idle.all.seconds:0}>0")
    public IdleStateHandler idleStateHandler() {
        return new IdleStateHandler(this.readTime, this.writeTime, this.allTime, TimeUnit.SECONDS);
    }

    // ------- WebSocket 协议相关ChannelHandler ----------------------------------


    /** HttpObjectAggregator 消息最大长度 **/
    @Value("${netty.server.websocket.http.aggregator.maxContentLength:65535}")
    private int maxContentLengthInAggregator;

    /** WebSocketFrameAggregator 消息最大长度 **/
    @Value("${netty.server.websocket.frame.aggregator.maxContentLength:65535}")
    private int maxContentLengthInFrame;

    /** websocket访问路径前缀 **/
    @Value("${netty.server.websocket.contextPath:/}")
    private String contextPath;


    /** HttpServerCodec：将请求和应答消息解码为HTTP消息 **/
    @Bean
    public HttpServerCodec httpServerCodec() {
        return new HttpServerCodec();
    }

    /** HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息 **/
    @Bean
    public HttpObjectAggregator httpObjectAggregator() {
        return new HttpObjectAggregator(this.maxContentLengthInAggregator);
    }

    /** 主要用于处理大数据流,防止因为大文件撑爆JVM **/
    @Bean
    public ChunkedWriteHandler chunkedWriteHandler() {
        return new ChunkedWriteHandler();
    }

    /** WebSocketFrameAggregator 通过对消息进行分类进行聚合,解码为WebSocket帧 **/
    @Bean
    public WebSocketFrameAggregator webSocketFrameAggregator() {
        return new WebSocketFrameAggregator(this.maxContentLengthInFrame);
    }

    /** WebSocket数据压缩 **/
    @Bean
    public WebSocketServerCompressionHandler webSocketServerCompressionHandler() {
        return new WebSocketServerCompressionHandler();
    }

    /** 处理websocket连接 **/
    @Bean
    public WebSocketServerProtocolHandler webSocketServerProtocolHandler() {
        return new WebSocketServerProtocolHandler(this.contextPath);
    }

}
