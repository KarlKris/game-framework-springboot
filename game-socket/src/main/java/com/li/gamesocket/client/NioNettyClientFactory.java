package com.li.gamesocket.client;

import com.li.gamecore.rpc.model.Address;
import com.li.gamesocket.client.channelhandler.NioNettyClientMessageHandler;
import com.li.gamesocket.client.impl.NioNettyClientImpl;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author li-yuanwen
 * NioNettyClient 工厂
 */
@Component
@Slf4j
public class NioNettyClientFactory {

    @Autowired
    private NioNettyClientMessageHandler messageHandler;

    /** 客户端连接超时毫秒配置 **/
    @Value("${netty.client.connect.timout.mills:3000}")
    private int connectTimeoutMills;
    /** 客户端共享IO线程组线程数量 **/
    @Value("${netty.client.eventLoopGroup.thread.num:4}")
    private int threadNum;

    /** 客户端共享线程组 **/
    private EventLoopGroup eventLoopGroup;

    /** 初始化线程组 **/
    private void initEventLoopGroup() {
        if (this.eventLoopGroup != null) {
            return;
        }

        synchronized (this) {
            if (this.eventLoopGroup != null) {
                return;
            }

            // 设置线程数量
            int i = Runtime.getRuntime().availableProcessors();
            this.eventLoopGroup = new NioEventLoopGroup(Math.min(i, threadNum));
        }

    }


    @PreDestroy
    private void destroy() {
        if (this.eventLoopGroup != null) {

            if (log.isDebugEnabled()) {
                log.debug("关闭客户端共享IO线程组");
            }

            this.eventLoopGroup.shutdownGracefully();
        }

    }


    /**
     * 构建客户端
     * @param address ip地址
     * @return 客户端
     */
    public NioNettyClient newInstance(Address address) {
        initEventLoopGroup();
        return NioNettyClientImpl.newInstance(address, this.connectTimeoutMills, this.eventLoopGroup, this.messageHandler);
    }
}
