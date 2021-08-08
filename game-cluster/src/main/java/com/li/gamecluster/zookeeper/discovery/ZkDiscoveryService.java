package com.li.gamecluster.zookeeper.discovery;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.gamecluster.zookeeper.config.CuratorFrameworkConfiguration;
import com.li.gamecluster.zookeeper.model.ServerType;
import com.li.gamecluster.zookeeper.model.ServiceDiscoveryNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author li-yuanwen
 * @date 2021/8/8 09:37
 * zookeeper 服务发现
 **/
@Service
@Slf4j
public class ZkDiscoveryService {

    @Autowired
    private CuratorFramework curatorFramework;
    @Autowired
    private CuratorFrameworkConfiguration config;
    @Autowired
    private ObjectMapper objectMapper;


    /** 服务节点缓存 **/
    private ConcurrentHashMap<String, ServiceDiscoveryNode> discoveryNodeCache;
    /** 模块2服务 **/
    private Map<Short, ServerType> module2Type = new HashMap<>();


    /** 获取某服务节点信息 **/
    public ServiceDiscoveryNode checkAndGetServiceDiscoveryNode(ServerType type) throws Exception {
        ServiceDiscoveryNode discoveryNode = null;
        if ((discoveryNode = this.discoveryNodeCache.get(type.getServiceName())) == null || !discoveryNode.isConnected()) {
            discoveryNode = new ServiceDiscoveryNode(type);

            // 处理并发
            ServiceDiscoveryNode old = this.discoveryNodeCache.putIfAbsent(type.getServiceName(), discoveryNode);
            if (old == null || !old.isConnected()) {
                this.discoveryNodeCache.put(type.getServiceName(), discoveryNode);
                discoveryNode.start(this.curatorFramework, bytes -> {
                    try {
                        synchronized (module2Type) {
                            for (short module : objectMapper.readValue(bytes
                                    , new TypeReference<Set<Short>>() {})) {
                                ServerType old1 = module2Type.putIfAbsent(module, type);
                                if (old1 != null) {
                                    log.warn("出现相同模块号[{}],不同服务[{},{}]"
                                            , module, old1.getServiceName(), type.getServiceName());
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                discoveryNode = old;
            }
        }

        return discoveryNode;
    }


    public ServiceDiscoveryNode checkAndGetServiceDiscoveryNodeByModule(short module) throws Exception {
        ServerType type = getServerTypeByModule(module);
        if (type == null) {
            return null;
        }
        return checkAndGetServiceDiscoveryNode(type);
    }

    public ServerType getServerTypeByModule(short module) {
        return this.module2Type.get(module);
    }



}
