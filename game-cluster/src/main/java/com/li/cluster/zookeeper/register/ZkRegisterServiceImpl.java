package com.li.cluster.zookeeper.register;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.cluster.zookeeper.config.CuratorFrameworkConfiguration;
import com.li.cluster.zookeeper.config.ZkConstant;
import com.li.cluster.zookeeper.model.ServiceInstancePayLoad;
import com.li.common.rpc.LocalServerService;
import com.li.common.rpc.ServerInfoUpdateService;
import com.li.common.rpc.model.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.zookeeper.CreateMode;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * zk 服务注册Service
 * @author li-yuanwen
 * @date 2021/8/7 16:14
 **/
@Slf4j
@Service
@ConditionalOnBean(LocalServerService.class)
public class ZkRegisterServiceImpl implements ServerInfoUpdateService, ApplicationRunner {

    /** 服务接口 **/
    @Resource
    private LocalServerService localServerService;

    /** zookeeper客户端 **/
    @Resource
    private CuratorFramework curatorFramework;
    @Resource
    private CuratorFrameworkConfiguration config;

    @Resource
    private ObjectMapper objectMapper;

    /** zookeeper curator #ServiceDisCorvery **/
    private ServiceDiscovery<ServiceInstancePayLoad> serviceDiscovery;
    /** 连接数节点路径 **/
    private String countPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (log.isInfoEnabled()) {
            log.info("Spring容器启动成功,开始注册服务");
        }

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
                .name(config.getServerType().name())
                .id(serverInfo.getId())
                .address(serverInfo.getIp())
                .port(serverInfo.getPort())
                .payload(new ServiceInstancePayLoad(serverInfo.getId()))
                .build();

        // 路径/discovery节点/服务名节点/具体服务节点
        this.serviceDiscovery.registerService(instance);

        // 创建记录连接数的临时节点
        // 路径/discovery节点/count节点/id
        this.countPath = discoveryPath
                + ZkConstant.ZOOKEEPER_SLASH
                + config.getServerType().name() + ZkConstant.SERVICE_COUNT_SUFFIX
                + ZkConstant.ZOOKEEPER_SLASH
                + serverInfo.getId();

        // 创建节点
        this.curatorFramework.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(this.countPath, objectMapper.writeValueAsBytes(0));

        // 更新/discovery数据 模块号数据
        this.curatorFramework.setData().forPath(discoveryPath, objectMapper.writeValueAsBytes(serverInfo.getModules()));

        if (log.isInfoEnabled()) {
            log.info("注册服务[{}]节点成功,模块数据{}", discoveryPath, serverInfo.getModules());
        }

    }


    /** 服务发现节点的路径(不包含根路径) **/
    private String toServiceDiscoveryPath() {
        return ZkConstant.ZOOKEEPER_SLASH + config.getServerType().name()
                + ZkConstant.SERVICE_DISCOVERY_SUFFIX;
    }

    @Override
    public void updateConnectNum(int connectNum) {
        try {
            this.curatorFramework.setData()
                    .forPath(this.countPath, objectMapper.writeValueAsBytes(connectNum));
        } catch (Exception e) {
            log.error("更新服务器连接数量出现未知异常", e);
        }
    }
}
