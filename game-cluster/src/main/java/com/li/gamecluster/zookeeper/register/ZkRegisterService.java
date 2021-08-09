package com.li.gamecluster.zookeeper.register;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.gamecluster.zookeeper.config.CuratorFrameworkConfiguration;
import com.li.gamecluster.zookeeper.config.ZkConstant;
import com.li.gamecluster.zookeeper.model.ServiceInstancePayLoad;
import com.li.gamecore.rpc.LocalServerService;
import com.li.gamecore.rpc.model.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author li-yuanwen
 * @date 2021/8/7 16:14
 * zk 服务注册Service
 **/
@Service
@Slf4j
public class ZkRegisterService {

    /** 服务接口 **/
    @Autowired(required = false)
    private LocalServerService localServerService;

    /** zookeeper客户端 **/
    @Autowired
    private CuratorFramework curatorFramework;
    @Autowired
    private CuratorFrameworkConfiguration config;

    @Autowired
    private ObjectMapper objectMapper;

    /** zookeeper curator #ServiceDisCorvery **/
    private ServiceDiscovery<ServiceInstancePayLoad> serviceDiscovery;
    /** 连接数节点路径 **/
    private String countPath;

    @PostConstruct
    private void init() throws Exception {
        if (this.localServerService == null) {
            return;
        }

        registerService(localServerService.getLocalServerInfo());

    }


    /**
     * 服务注册
     * @param serverInfo 服务器信息
     */
    public void registerService(ServerInfo serverInfo) throws Exception {
        // 防止重复注册
        if (this.serviceDiscovery != null) {
            return;
        }

        String discoveryPath = toServiceDiscoveryPath();

        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInstancePayLoad.class)
                .basePath(discoveryPath)
                .serializer(ZkConstant.SERIALIZER)
                .client(this.curatorFramework)
                .build();

        this.serviceDiscovery.start();

        // ServiceInstancePayLoad暂时没有数据可填写,后续有的话再加
        ServiceInstance<ServiceInstancePayLoad> instance = ServiceInstance.<ServiceInstancePayLoad>builder()
                .name(config.getServerType().getServiceName())
                .id(serverInfo.getId())
                .address(serverInfo.getId())
                .port(serverInfo.getPort())
                .build();

        // 路径/discovery节点/服务名节点/具体服务节点
        this.serviceDiscovery.registerService(instance);

        // 创建记录连接数的临时节点
        // 路径/discovery节点/count节点/id
        this.countPath = discoveryPath
                + ZkConstant.ZOOKEEPER_SLASH
                + config.getServerType().getServiceName() + ZkConstant.SERVICE_COUNT_SUFFIX
                + ZkConstant.ZOOKEEPER_SLASH
                + serverInfo.getId();

        // 创建节点
        this.curatorFramework.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(this.countPath, new byte[] {0});

        // 更新/discovery数据 模块号数据
        this.curatorFramework.setData().forPath(discoveryPath, objectMapper.writeValueAsBytes(serverInfo.getModules()));

    }


    /** 服务发现节点的路径(不包含根路径) **/
    private String toServiceDiscoveryPath() {
        return ZkConstant.ZOOKEEPER_SLASH + config.getServerType().getServiceName()
                + ZkConstant.SERVICE_DISCOVERY_SUFFIX;
    }



}
