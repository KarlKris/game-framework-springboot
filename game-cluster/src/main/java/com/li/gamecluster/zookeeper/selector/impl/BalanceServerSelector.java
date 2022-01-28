package com.li.gamecluster.zookeeper.selector.impl;

import com.li.gamecluster.zookeeper.model.ServiceDiscoveryNode;
import com.li.gamecluster.zookeeper.selector.ServerSelector;
import com.li.gamecommon.rpc.model.Address;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 基于负载均衡选择服务
 * @author li-yuanwen
 * @date 2021/8/8 10:45
 **/
@Slf4j
public class BalanceServerSelector implements ServerSelector {

    @Override
    public Address select(ServiceDiscoveryNode node, long identity) {
        Map<String, Address> cache = node.getAddressCache();
        if (cache.size() == 0) {
            return null;
        }
        if (cache.size() == 1) {
            return cache.values().stream().findFirst().get();
        }

        try {
            return cache.get(node.checkAndGetMinServiceCountInstanceId());
        } catch (Exception e) {
            log.error("服务类型[{}]负载均衡选择出现未知异常", node.getType().name(), e);
            return null;
        }
    }
}
