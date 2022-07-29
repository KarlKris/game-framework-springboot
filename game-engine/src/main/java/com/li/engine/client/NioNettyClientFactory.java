package com.li.engine.client;

import cn.hutool.core.thread.NamedThreadFactory;
import com.li.common.rpc.model.Address;
import com.li.common.shutdown.ShutdownProcessor;
import com.li.engine.client.impl.DefaultNettyClient;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author li-yuanwen
 * NioNettyClient 工厂
 */
@Slf4j
@Component
public class NioNettyClientFactory implements ShutdownProcessor {

    /** 客户端连接管理 **/
    private final Map<Address, NettyClient> clients = new ConcurrentHashMap<>(8);

    /** 客户端连接超时毫秒配置 **/
    @Value("${netty.client.connect.timout.mills:3000}")
    private int connectTimeoutMills;
    /** 客户端共享IO线程组线程数量 **/
    @Value("${netty.client.eventLoopGroup.thread.num:4}")
    private int threadNum;


    /** 客户端共享线程组 **/
    private EventLoopGroup eventLoopGroup;

    /** 初始化线程组 **/
    private void checkAndInitEventLoopGroup() {
        if (this.eventLoopGroup != null) {
            return;
        }

        synchronized (this) {
            if (this.eventLoopGroup != null) {
                return;
            }

            // 设置线程数量
            int i = Runtime.getRuntime().availableProcessors();
            this.eventLoopGroup = new NioEventLoopGroup(Math.min(i, threadNum)
                    , new NamedThreadFactory("Netty-Client-Thread", false));
        }

    }

    @Override
    public int getOrder() {
        return ShutdownProcessor.SHUT_DOWN_CLIENT_POOL;
    }

    @Override
    public void shutdown() {
        if (this.eventLoopGroup != null) {
            this.eventLoopGroup.shutdownGracefully();
            log.warn("关闭客户端共享IO线程组");
            clients.clear();
        }
    }



    /**
     * 连接目标地址
     * @param address ip地址
     * @return 客户端
     */
    public NettyClient newInstance(Address address) {
        return this.clients.computeIfAbsent(address, addr -> {
            checkAndInitEventLoopGroup();
            return DefaultNettyClient.newInstance(addr
                    , connectTimeoutMills
                    , eventLoopGroup);
        });
    }
}
