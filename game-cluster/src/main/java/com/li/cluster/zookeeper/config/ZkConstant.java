package com.li.cluster.zookeeper.config;

import com.li.cluster.zookeeper.model.InstanceDetails;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

/**
 * @author li-yuanwen
 * @date 2021/8/7 20:08
 * zookeeper使用的常量
 **/
public interface ZkConstant {

    JsonInstanceSerializer<InstanceDetails> SERIALIZER = new JsonInstanceSerializer<>(InstanceDetails.class);

    /** zookeeper 路径划分符号 **/
    String ZOOKEEPER_SLASH = "/";

    /** 服务发现名称 **/
    String SERVICE_DISCOVERY = "DISCOVERY";

    /** 连接数名称 **/
    String SERVICE_CONNECT_COUNT = "CONNECT_COUNT";

    /** 模块号 **/
    String SERVICE_MODULES = "MODULES";

}
