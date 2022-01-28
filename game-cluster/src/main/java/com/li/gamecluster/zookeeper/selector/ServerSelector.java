package com.li.gamecluster.zookeeper.selector;

import com.li.gamecluster.zookeeper.model.ServiceDiscoveryNode;
import com.li.gamecluster.zookeeper.selector.impl.BalanceServerSelector;
import com.li.gamecluster.zookeeper.selector.impl.IdentityServerSelector;
import com.li.gamecommon.rpc.model.Address;

/**
 * @author li-yuanwen
 * @date 2021/8/8 10:41
 * 服务集群选择器
 **/
public interface ServerSelector {

    /** 负载均衡选择器 **/
    ServerSelector BALANCE_SELECTOR = new BalanceServerSelector();
    /** 身份标识选择器 **/
    ServerSelector IDENTITY_SELECTOR = new IdentityServerSelector();


    /**
     * 根据身份标识筛选服务
     * @param node 筛选服务目标节点
     * @param identity 身份标识
     * @return 服务
     */
    Address select(ServiceDiscoveryNode node, long identity);


}
