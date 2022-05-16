package com.li.cluster.zookeeper.model;

import cn.hutool.core.util.ByteUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.cluster.zookeeper.config.ZkConstant;
import com.li.cluster.zookeeper.util.CuratorUtil;
import com.li.common.rpc.model.Address;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author li-yuanwen
 * @date 2021/8/8 10:20
 * 服务发现节点数据
 **/
@Slf4j
public class ServiceDiscoveryNode {

    /** 服务名称 **/
    @Getter
    private final ServerType type;

    /** json序列化与反序列化工具 **/
    private final ObjectMapper objectMapper;

    /** 连接状态 **/
    private boolean connected;

    /** 客户端 **/
    private CuratorFramework curatorFramework;

    /** 服务发现 **/
    private ServiceDiscovery<InstanceDetails> discovery;

    /** 服务缓存 **/
    private ServiceCache<InstanceDetails> cache;

    /** 服务地址缓存 **/
    @Getter
    private Map<String, Address> addressCache;

    /** 当前连接第二少的连接数量 **/
    private int lastConnectNum;

    /** 当前最少连接数节点id **/
    private String minConnectInstanceId;

    public ServiceDiscoveryNode(ServerType type, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.type = type;
    }

    /** 关闭 **/
    public void close() throws IOException {
        if (this.discovery == null) {
            return;
        }

        CloseableUtils.closeQuietly(this.cache);
        CloseableUtils.closeQuietly(this.discovery);
    }

    public void start(CuratorFramework curatorFramework) throws Exception {
        if (this.discovery != null) {
            return;
        }

        this.curatorFramework = curatorFramework;

        String discoveryPath = toDiscoveryPath();
        this.discovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                .client(curatorFramework)
                .basePath(discoveryPath)
                .serializer(ZkConstant.SERIALIZER)
                .build();

        this.discovery.start();

        this.cache = this.discovery.serviceCacheBuilder().name(type.name()).build();
        this.cache.start();
        initInstanceAddressCache();

        this.cache.addListener(new ServiceCacheListener() {
            @Override
            public void cacheChanged() {
                initInstanceAddressCache();
                if (log.isWarnEnabled()) {
                    log.warn("服务集群[{}]状态发生变更,集群数量[{}]", type.name(), addressCache.size());
                }
            }

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (log.isWarnEnabled()) {
                    log.warn("服务集群[{}]状态发生变更[{}]", type.name(), newState.name());
                }
                connected = newState.isConnected();
            }
        });

        this.connected = true;

    }

    /** 服务是否正常 **/
    public boolean isConnected() {
        return this.connected;
    }

    private void initInstanceAddressCache() {
        Map<String, Address> tempAddress = new HashMap<>(8);
        for (ServiceInstance<InstanceDetails> instance : this.cache.getInstances()) {
            tempAddress.put(instance.getId(), new Address(instance.getAddress(), instance.getPort()));
        }
        this.addressCache = Collections.unmodifiableMap(tempAddress);
    }

    /** 获取负责的模块号 **/
    public Set<Short> getModules() throws Exception {
        if (!isConnected()) {
            throw new RuntimeException("节点未连接");
        }

        String modulesPath = ZkConstant.ZOOKEEPER_SLASH
                + ZkConstant.SERVICE_MODULES
                + ZkConstant.ZOOKEEPER_SLASH
                + type.name();

        return CuratorUtil.getData(curatorFramework, modulesPath, objectMapper, new TypeReference<Set<Short>>() {});
    }

    /** 获取负载量最小的连接节点id **/
    public String checkAndGetMinServiceCountInstanceId() throws Exception {
        String countPath = toCountPathPrefix();

        boolean change = true;
        if (this.minConnectInstanceId != null) {
            byte[] bytes = this.curatorFramework.getData()
                    .forPath(countPath + ZkConstant.ZOOKEEPER_SLASH + minConnectInstanceId);
            int curCount = ByteUtil.bytesToInt(bytes);
            change = curCount < this.lastConnectNum;
        }

        if (!change) {
            return minConnectInstanceId;
        }

        doSearchMinCountServiceInstanceId(countPath);

        return minConnectInstanceId;
    }

    /** 获取总共连接数 **/
    public int getTotalCount() throws Exception {
        String countPath = toCountPathPrefix();
        int count = 0;
        for (String id : this.curatorFramework.getChildren().forPath(countPath)) {
            byte[] bytes = this.curatorFramework.getData().forPath(countPath + ZkConstant.ZOOKEEPER_SLASH + id);
            count += ByteUtil.bytesToInt(bytes);
        }
        return count;
    }

    /** 获取某个服务的连接数 **/
    public int getCount(String id) throws Exception {
        String countPath = toCountPathPrefix()+ ZkConstant.ZOOKEEPER_SLASH + id;
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

    private void doSearchMinCountServiceInstanceId(String countPathPrefix) throws Exception {
        int min = Integer.MAX_VALUE;
        int lastButOne = Integer.MAX_VALUE;
        String selectedInstanceId = null;
        for (String id : this.curatorFramework.getChildren().forPath(countPathPrefix)) {
            byte[] bytes = this.curatorFramework.getData().forPath(countPathPrefix + ZkConstant.ZOOKEEPER_SLASH + id);
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
        this.lastConnectNum = lastButOne;
    }


    public String toDiscoveryPath() {
        return ZkConstant.SERVICE_DISCOVERY;
    }

    private String toCountPathPrefix() {
        return ZkConstant.ZOOKEEPER_SLASH
                + ZkConstant.SERVICE_CONNECT_COUNT
                + ZkConstant.ZOOKEEPER_SLASH
                + type.name();
    }
}
