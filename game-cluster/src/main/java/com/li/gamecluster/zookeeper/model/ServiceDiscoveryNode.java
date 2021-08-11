package com.li.gamecluster.zookeeper.model;

import cn.hutool.core.util.ByteUtil;
import com.li.gamecluster.zookeeper.config.ZkConstant;
import com.li.gamecore.rpc.model.Address;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author li-yuanwen
 * @date 2021/8/8 10:20
 * 服务发现节点数据
 **/
@Slf4j
public class ServiceDiscoveryNode {

    /** 服务名称 **/
    @Getter
    private ServerType type;

    /** 连接状态 **/
    @Getter
    private boolean connected;

    /** 客户端 **/
    private CuratorFramework curatorFramework;

    /** 服务发现 **/
    private ServiceDiscovery<ServiceInstancePayLoad> discovery;

    /** 服务缓存 **/
    private ServiceCache<ServiceInstancePayLoad> cache;

    /** 服务地址缓存 **/
    @Getter
    private Map<String, Address> addressCache;

    /** 当前连接第二少的连接数量 **/
    private int lastSecondConnectNum;

    /** 当前最少连接数节点id **/
    private String minConnectInstanceId;

    public ServiceDiscoveryNode(ServerType type) {
        this.type = type;
    }

    /** 关闭 **/
    public void close() throws IOException {
        if (this.discovery == null) {
            return;
        }

        this.cache.close();
        this.discovery.close();
    }

    public void start(CuratorFramework curatorFramework, Consumer<byte[]> consumer) throws Exception {
        if (this.discovery != null) {
            return;
        }

        this.curatorFramework = curatorFramework;

        String discoveryPath = toDiscoveryPath();
        this.discovery = ServiceDiscoveryBuilder.builder(ServiceInstancePayLoad.class)
                .client(curatorFramework)
                .basePath(discoveryPath)
                .serializer(ZkConstant.SERIALIZER)
                .build();

        this.discovery.start();

        this.cache = this.discovery.serviceCacheBuilder().name(type.getServiceName()).build();

        this.cache.addListener(new ServiceCacheListener() {
            @Override
            public void cacheChanged() {
                initInstanceAddressCache();
                if (log.isWarnEnabled()) {
                    log.warn("服务集群[{}]状态发生变更,集群数量[{}]", type.getServiceName(), addressCache.size());
                }
            }

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (log.isWarnEnabled()) {
                    log.warn("服务集群[{}]状态发生变更[{}]", type.getServiceName(), newState.name());
                }
                // todo 后续改进处理断开连接的情况
                connected = newState.isConnected();

            }
        });

        this.cache.start();;
        initInstanceAddressCache();

        // 获取模块数据
        consumer.accept(this.curatorFramework.getData().forPath(discoveryPath));

        this.connected = true;

    }

    /** 服务是否正常 **/
    public boolean isConnected() {
        return this.connected;
    }

    private void initInstanceAddressCache() {
        Map<String, Address> tempAddress = new HashMap<>(8);
        for (ServiceInstance<ServiceInstancePayLoad> instance : this.cache.getInstances()) {
            tempAddress.put(instance.getId(), new Address(instance.getAddress(), instance.getPort()));
        }
        this.addressCache = Collections.unmodifiableMap(tempAddress);
    }

    /** 获取负载量最小的连接节点id **/
    public String checkAndGetMinServiceCountInstanceId() throws Exception {
        String countPath = toCountPath();

        boolean change = true;
        if (this.minConnectInstanceId != null) {
            byte[] bytes = this.curatorFramework.getData().forPath(countPath + ZkConstant.ZOOKEEPER_SLASH + minConnectInstanceId);
            int curCount = ByteUtil.bytesToInt(bytes);
            change = curCount < this.lastSecondConnectNum;
        }

        if (!change) {
            return minConnectInstanceId;
        }

        doSearchMinCountServiceInstanceId(countPath);

        return minConnectInstanceId;
    }

    /** 获取总共连接数 **/
    public int getTotalCount() throws Exception {
        String countPath = toCountPath();
        int count = 0;
        for (String id : this.curatorFramework.getChildren().forPath(countPath)) {
            byte[] bytes = this.curatorFramework.getData().forPath(countPath + ZkConstant.ZOOKEEPER_SLASH + id);
            count += ByteUtil.bytesToInt(bytes);
        }
        return count;
    }

    /** 获取某个服务的连接数 **/
    public int getCount(String id) throws Exception {
        String countPath = toCountPath()+ ZkConstant.ZOOKEEPER_SLASH + id;
        return ByteUtil.bytesToInt(this.curatorFramework.getData().forPath(countPath));
    }

    /** 根据身份标识选择 **/
    public Address selectAddress(long identity) {
        return type.getSelector().select(this, identity);
    }

    /** 根据服标识选择 **/
    public Address selectAddressById(String id) {
        return addressCache.get(id);
    }

    private void doSearchMinCountServiceInstanceId(String countPath) throws Exception {
        int min = Integer.MAX_VALUE;
        int lastButOne = Integer.MAX_VALUE;
        String selectedInstanceId = null;
        for (String id : this.curatorFramework.getChildren().forPath(countPath)) {
            byte[] bytes = this.curatorFramework.getData().forPath(countPath + ZkConstant.ZOOKEEPER_SLASH + id);
            int curCount = ByteUtil.bytesToInt(bytes);
            if (min > curCount) {
                min = curCount;
                selectedInstanceId = id;
            }
            if (lastButOne > curCount) {
                lastButOne = curCount;
            }
        }

        this.minConnectInstanceId = selectedInstanceId;
        this.lastSecondConnectNum = lastButOne;
    }


    public String toDiscoveryPath() {
        return type.getServiceName() + ZkConstant.SERVICE_DISCOVERY_SUFFIX;
    }

    private String toCountPath() {
        return toDiscoveryPath()
                + ZkConstant.ZOOKEEPER_SLASH
                + type.getServiceName() + ZkConstant.SERVICE_COUNT_SUFFIX;
    }
}
