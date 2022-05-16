package com.li.cluster.zookeeper.register;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.cluster.zookeeper.config.ZkConstant;
import com.li.cluster.zookeeper.model.InstanceDetails;
import com.li.cluster.zookeeper.model.ServerType;
import com.li.cluster.zookeeper.util.CuratorUtil;
import com.li.common.rpc.LocalServerService;
import com.li.common.rpc.ServerInfoUpdateService;
import com.li.common.rpc.model.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
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
public class ZkRegisterServiceImpl implements ServerInfoUpdateService, ApplicationRunner, ApplicationListener<ContextClosedEvent> {

    /** 服务名称 **/
    @Value("${zookeeper.server.serviceName}")
    private ServerType thisType;

    /** 服务接口 **/
    @Resource
    private LocalServerService localServerService;

    /** zookeeper客户端 **/
    @Resource
    private CuratorFramework curatorFramework;


    @Resource
    private ObjectMapper objectMapper;

    /** 连接数节点路径 **/
    private String countPath;
    /** service discovery  **/
    private ServiceDiscovery<InstanceDetails> discovery;

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
        if (discovery != null) {
            return;
        }

        // 路径/discovery节点/服务名节点/具体服务节点
        // ServiceInstancePayLoad暂时没有数据可填写,后续有的话再加
        ServiceInstance<InstanceDetails> instance = ServiceInstance.<InstanceDetails>builder()
                .name(thisType.name())
                .id(serverInfo.getId())
                .address(serverInfo.getIp())
                .port(serverInfo.getPort())
                .payload(new InstanceDetails(serverInfo.getId()))
                .build();

        String discoveryPath = ZkConstant.ZOOKEEPER_SLASH + ZkConstant.SERVICE_DISCOVERY;

        discovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                .basePath(discoveryPath)
                .serializer(ZkConstant.SERIALIZER)
                .client(this.curatorFramework)
                .thisInstance(instance)
                .build();

        discovery.start();


        // 创建记录连接数的临时节点
        // 路径/count节点/serviceName/id
        countPath = ZkConstant.ZOOKEEPER_SLASH
                + ZkConstant.SERVICE_CONNECT_COUNT
                + ZkConstant.ZOOKEEPER_SLASH
                + thisType.name()
                + ZkConstant.ZOOKEEPER_SLASH
                + serverInfo.getId();

        // 创建节点
        CuratorUtil.createEphemeralNode(curatorFramework, countPath, objectMapper.writeValueAsBytes(0));

        // 模块号节点
        // modules/serviceName
        String modulesPath = ZkConstant.ZOOKEEPER_SLASH
                + ZkConstant.SERVICE_MODULES
                + ZkConstant.ZOOKEEPER_SLASH
                + thisType.name();

        // 创建节点
        CuratorUtil.createEphemeralNode(curatorFramework, modulesPath, objectMapper.writeValueAsBytes(serverInfo.getModules()));

        if (log.isInfoEnabled()) {
            log.info("注册服务[{}]节点成功,模块数据{}", discoveryPath, serverInfo.getModules());
        }



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

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // 关闭资源
        CloseableUtils.closeQuietly(discovery);
    }
}
