package com.li.gamecluster.zookeeper.selector.impl;

import com.li.gamecluster.zookeeper.model.ServiceDiscoveryNode;
import com.li.gamecluster.zookeeper.selector.ServiceSelector;
import com.li.gamecore.common.SnowflakeIdGenerator;
import com.li.gamecore.rpc.model.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * @date 2021/8/8 10:48
 * 基于身份标识选择服务
 **/
@Component
@Slf4j
public class IdentityServiceSelector implements ServiceSelector {

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
