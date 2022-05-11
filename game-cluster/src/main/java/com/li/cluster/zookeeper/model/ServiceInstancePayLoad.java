package com.li.cluster.zookeeper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 上传到zk的服务器相关信息
 * @author li-yuanwen
 * @date 2021/8/7 21:43
 **/
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstancePayLoad {

    /**  **/
    private String serverId;

}
