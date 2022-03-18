package com.li.gamecommon.resource.storage;

import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 配表资源接口
 * @author li-yuanwen
 * @date 2022/1/24
 */
public interface ResourceStorage<K, V extends TableResource<K>> {

    /**
     * 获取配表资源
     * @param id 一行表的唯一标识
     * @return 配表对象
     */
    @Nullable
    V getResource(K id);

    /**
     * 获取指定的唯一索引值
     * @param uniqueName 唯一索引名
     * @param uniqueKey 唯一索引key
     * @return 唯一索引值 or null
     */
    @Nullable
    V getUniqueResource(String uniqueName, Object uniqueKey);

    /**
     * 获取指定的索引内容
     * @param indexName 索引名
     * @param indexKey 索引key
     * @return  索引内容
     */
    List<V> getIndexResources(String indexName, Object indexKey);

    /**
     * 获取所有资源
     * @return 所有资源
     */
    List<V> getAll();

    /**
     * 初始化资源
     */
    void initializeStorage();

    /**
     * 添加变更监听器
     * @param listener 监听器
     */
    void addListener(StorageChangeListener listener);

}
