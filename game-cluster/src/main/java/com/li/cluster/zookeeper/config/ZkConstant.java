package com.li.cluster.zookeeper.config;

import com.li.cluster.zookeeper.model.ServiceInstancePayLoad;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

/**
 * @author li-yuanwen
 * @date 2021/8/7 20:08
 * zookeeper使用的常量
 **/
public interface ZkConstant {

    JsonInstanceSerializer<ServiceInstancePayLoad> SERIALIZER = new JsonInstanceSerializer<>(ServiceInstancePayLoad.class);

    /** zookeeper 路径划分符号 **/
    String ZOOKEEPER_SLASH = "/";

    /** 服务发现名称后缀 **/
    String SERVICE_DISCOVERY_SUFFIX = "_DISCOVERY";

    /** 连接数节点父节点名称后缀 **/
    String SERVICE_COUNT_SUFFIX = "_COUNT";

}
