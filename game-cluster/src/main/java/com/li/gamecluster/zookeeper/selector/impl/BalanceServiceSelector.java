package com.li.gamecluster.zookeeper.selector.impl;

import com.li.gamecluster.zookeeper.model.ServiceDiscoveryNode;
import com.li.gamecluster.zookeeper.model.ServiceInstancePayLoad;
import com.li.gamecluster.zookeeper.selector.ServiceSelector;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @author li-yuanwen
 * @date 2021/8/8 10:45
 * 基于负载均衡选择服务
 **/
public class BalanceServiceSelector implements ServiceSelector {

    @Override
    public ServiceInstance<ServiceInstancePayLoad> select(ServiceDiscoveryNode node, long identity) {
        return null;
    }
}
