package com.li.gamecluster.zookeeper.selector;

import com.li.gamecluster.zookeeper.model.ServiceDiscoveryNode;
import com.li.gamecluster.zookeeper.model.ServiceInstancePayLoad;
import com.li.gamecluster.zookeeper.selector.impl.BalanceServiceSelector;
import com.li.gamecluster.zookeeper.selector.impl.IdentityServiceSelector;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @author li-yuanwen
 * @date 2021/8/8 10:41
 * 服务集群选择器
 **/
public interface ServiceSelector {

    /** 负载均衡选择器 **/
    ServiceSelector BALANCE_SELECTOR = new BalanceServiceSelector();
    /** 身份标识选择器 **/
    ServiceSelector IDENTITY_SELECTOR = new IdentityServiceSelector();


    /**
     * 根据身份标识筛选服务
     * @param node 筛选服务目标节点
     * @param identity 身份标识
     * @return 服务
     */
    ServiceInstance<ServiceInstancePayLoad> select(ServiceDiscoveryNode node, long identity);


}
