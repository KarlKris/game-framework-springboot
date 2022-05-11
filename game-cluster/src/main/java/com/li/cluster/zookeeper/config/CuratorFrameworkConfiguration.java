package com.li.cluster.zookeeper.config;

import com.li.cluster.zookeeper.model.ServerType;
import lombok.Getter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author li-yuanwen
 * @date 2021/8/7 15:29
 * CuratorFramework 配置
 **/
@Configuration
public class CuratorFrameworkConfiguration {

    /** zookeeper server 地址 **/
    @Value("${zookeeper.url}")
    private String zookeeperUrl;

    /** 命名空间 **/
    @Value("${zookeeper.namespace}")
    private String nameSpace;

    /** 服务名称 **/
    @Getter
    @Value("${zookeeper.server.serviceName}")
    private ServerType serverType;


    @Bean
    public CuratorFramework curatorFramework() {
        // 两次重连的等待的时间为60s 重连次数上限是10次
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(6000, 10);
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(this.zookeeperUrl)
                .retryPolicy(retry)
                .namespace(this.nameSpace)
                .build();

        curatorFramework.start();
        return curatorFramework;
    }


}
