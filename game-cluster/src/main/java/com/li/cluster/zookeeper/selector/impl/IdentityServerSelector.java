package com.li.cluster.zookeeper.selector.impl;

import com.li.cluster.zookeeper.model.ServiceDiscoveryNode;
import com.li.cluster.zookeeper.selector.ServerSelector;
import com.li.common.id.SnowflakeIdGenerator;
import com.li.common.rpc.model.Address;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于身份标识选择服务
 * @author li-yuanwen
 * @date 2021/8/8 10:48
 **/
@Slf4j
public class IdentityServerSelector implements ServerSelector {

    @Override
    public Address select(ServiceDiscoveryNode node, long identity) {
        if (identity <= 0) {
            return null;
        }
        String workerId = String.valueOf(SnowflakeIdGenerator.toWorkerId(identity));
        String serverId = node.getAddressCache()
                .keySet()
                .stream()
                .filter(workerId::equals)
                .findAny()
                .orElse(null);
        if (serverId == null) {
            return null;
        }

        return node.getAddressCache().get(serverId);
    }
}
