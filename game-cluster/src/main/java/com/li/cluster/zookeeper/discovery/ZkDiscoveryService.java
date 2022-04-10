package com.li.cluster.zookeeper.discovery;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.cluster.zookeeper.model.ServerType;
import com.li.cluster.zookeeper.model.ServiceDiscoveryNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
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
public class ZkDiscoveryService implements ApplicationListener<ContextClosedEvent> {

    @Resource
    private CuratorFramework curatorFramework;
    @Resource
    private ObjectMapper objectMapper;


    /** 服务节点缓存 **/
    private final ConcurrentHashMap<String, ServiceDiscoveryNode> discoveryNodeCache = new ConcurrentHashMap<>(ServerType.values().length);
    /** 模块2服务 **/
    private final Map<Short, ServerType> module2Type = new HashMap<>();


    /** 获取某服务节点信息 **/
    public ServiceDiscoveryNode checkAndGetServiceDiscoveryNode(ServerType type) throws Exception {
        ServiceDiscoveryNode discoveryNode = null;
        if ((discoveryNode = this.discoveryNodeCache.get(type.name())) == null || !discoveryNode.isConnected()) {
            discoveryNode = new ServiceDiscoveryNode(type);

            // 处理并发
            ServiceDiscoveryNode old = this.discoveryNodeCache.putIfAbsent(type.name(), discoveryNode);
            if (old == null || !old.isConnected()) {
                this.discoveryNodeCache.put(type.name(), discoveryNode);
                discoveryNode.start(this.curatorFramework, bytes -> {
                    try {
                        synchronized (module2Type) {
                            for (short module : objectMapper.readValue(bytes
                                    , new TypeReference<Set<Short>>() {})) {
                                ServerType old1 = module2Type.putIfAbsent(module, type);
                                if (old1 != null) {
                                    log.warn("出现相同模块号[{}],不同服务[{},{}]"
                                            , module, old1.name(), type.name());
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

    public ServerType getServerTypeByModule(short module) throws Exception {
        ServerType type = this.module2Type.get(module);
        if (type != null) {
            return type;
        }
        for (ServerType serverType : ServerType.values()) {
            checkAndGetServiceDiscoveryNode(serverType);
        }
        return this.module2Type.get(module);
    }


    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        if (CollectionUtils.isEmpty(discoveryNodeCache)) {
            return;
        }
        for (ServiceDiscoveryNode serviceDiscoveryNode : discoveryNodeCache.values()) {
            try {
                serviceDiscoveryNode.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
