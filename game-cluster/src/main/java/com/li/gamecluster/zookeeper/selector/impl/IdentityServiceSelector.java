package com.li.gamecluster.zookeeper.selector.impl;

import com.li.gamecluster.zookeeper.model.ServiceDiscoveryNode;
import com.li.gamecluster.zookeeper.model.ServiceInstancePayLoad;
import com.li.gamecluster.zookeeper.selector.ServiceSelector;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @author li-yuanwen
 * @date 2021/8/8 10:48
 * 基于身份标识选择服务
 **/
public class IdentityServiceSelector implements ServiceSelector {

    @Override
    public ServiceInstance<ServiceInstancePayLoad> select(ServiceDiscoveryNode node, long identity) {
        return null;
    }
}
