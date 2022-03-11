package com.li.gamecluster.zookeeper.selector.impl;

import com.li.gamecluster.zookeeper.model.ServiceDiscoveryNode;
import com.li.gamecluster.zookeeper.selector.ServerSelector;
import com.li.gamecommon.rpc.model.Address;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于id标识哈希选择服务
 * @author li-yuanwen
 * @date 2022/3/9
 */
@Slf4j
public class HashServerSelector implements ServerSelector {

    @Override
    public Address select(ServiceDiscoveryNode node, long identity) {
        Map<String, Address> cache = node.getAddressCache();
        List<String> list = cache.keySet().stream().sorted().collect(Collectors.toList());
        if (list.size() == 1) {
            return cache.get(list.get(0));
        }

        int index = (int) identity % list.size();

        return cache.get(list.get(index));
    }
}
