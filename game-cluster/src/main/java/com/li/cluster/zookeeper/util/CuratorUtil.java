package com.li.cluster.zookeeper.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

/**
 * Zookeeper客户端Curator相关操作封装
 * @author li-yuanwen
 * @date 2022/5/16
 */
public class CuratorUtil {

    /**
     * 创建临时节点
     * @param curatorFramework 客户端
     * @param path 路径
     * @param jsonData data
     */
    public static void createEphemeralNode(CuratorFramework curatorFramework, String path, byte[] jsonData) throws Exception {
        curatorFramework.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, jsonData);
    }

    /**
     * 获取节点数据
     * @param curatorFramework 客户端
     * @param path 路径
     * @param objectMapper 反序列化工具
     * @param typeReference 反序列化对象
     * @param <T> 具体类型
     * @return 节点数据
     * @throws Exception 节点路径不存在或反序列化失败时抛出
     */
    public static <T> T getData(CuratorFramework curatorFramework, String path
            , ObjectMapper objectMapper, TypeReference<T> typeReference) throws Exception {
        byte[] bytes = curatorFramework.getData().forPath(path);
        return objectMapper.readValue(bytes, typeReference);
    }


}
