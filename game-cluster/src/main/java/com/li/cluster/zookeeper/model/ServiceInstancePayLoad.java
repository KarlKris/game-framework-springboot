package com.li.cluster.zookeeper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author li-yuanwen
 * @date 2021/8/7 21:43
 * ServiceDiscovery<ServiceInstance>
 **/
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstancePayLoad {

    private String serverId;

}
