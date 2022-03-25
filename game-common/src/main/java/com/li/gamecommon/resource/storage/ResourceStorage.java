package com.li.gamecommon.resource.storage;

import org.springframework.lang.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * 配表资源接口
 * @author li-yuanwen
 * @date 2022/1/24
 */
public interface ResourceStorage<K, V> {

    String FILE_SPLIT = ".";
    String FILE_PATH = File.separator;

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
    Collection<V> getAll();

    /**
     * 加载资源(此时并不会覆盖原数据,需要调用#validate()来检验，通过后覆盖原数据)
     */
    void load();

    /**
     * 验证资源合法性,通过后覆盖原数据
     */
    void validate();

    /**
     * 添加变更监听器
     * @param listener 监听器
     */
    void addListener(StorageChangeListener listener);

}
