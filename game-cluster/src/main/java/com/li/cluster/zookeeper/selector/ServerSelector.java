package com.li.cluster.zookeeper.selector;

import com.li.cluster.zookeeper.model.ServiceDiscoveryNode;
import com.li.cluster.zookeeper.selector.impl.BalanceServerSelector;
import com.li.cluster.zookeeper.selector.impl.HashServerSelector;
import com.li.cluster.zookeeper.selector.impl.IdentityServerSelector;
import com.li.common.rpc.model.Address;

/**
 * 服务集群选择器
 * @author li-yuanwen
 * @date 2021/8/8 10:41
 **/
public interface ServerSelector {

    /** 负载均衡选择器 **/
    ServerSelector BALANCE_SELECTOR = new BalanceServerSelector();
    /** 身份标识选择器 **/
    ServerSelector IDENTITY_SELECTOR = new IdentityServerSelector();
    /** 标识哈希选择器 **/
    ServerSelector HASH_SELECTOR = new HashServerSelector();


    /**
     * 根据身份标识筛选服务
     * @param node 筛选服务目标节点
     * @param identity 身份标识
     * @return 服务
     */
    Address select(ServiceDiscoveryNode node, long identity);


}
