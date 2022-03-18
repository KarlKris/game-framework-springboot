package com.li.gamecommon.resource.storage;

import com.li.gamecommon.resource.core.ResourceDefinition;
import com.li.gamecommon.resource.reader.ResourceReader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 默认资源容器
 * @author li-yuanwen
 * @date 2022/3/17
 */
public class DefaultResourceStorage<K, V extends TableResource<K>> implements ResourceStorage<K, V> {

    /** 文件资源信息 **/
    private final ResourceDefinition resourceDefinition;
    /** 文件资源读取器 **/
    private final ResourceReader resourceReader;
    /** 数据容器 **/
    private InnerValueHolder data;
    /** 数据变更监听器 **/
    private final List<StorageChangeListener> listeners = new LinkedList<>();

    public DefaultResourceStorage(ResourceDefinition resourceDefinition, ResourceReader resourceReader) {
        this.resourceDefinition = resourceDefinition;
        this.resourceReader = resourceReader;
    }

    @Override
    public V getResource(K id) {
        return null;
    }

    @Override
    public V getUniqueResource(String uniqueName, Object uniqueKey) {
        return null;
    }

    @Override
    public List<V> getIndexResources(String indexName, Object indexKey) {
        return null;
    }

    @Override
    public List<V> getAll() {
        return null;
    }

    @Override
    public void initializeStorage() {
        load();
    }

    @Override
    public void addListener(StorageChangeListener listener) {
        listeners.add(listener);
    }

    // -------------- 私有方法 --------------------------------

    /** 文件资源数据读取 **/
    private void load() {

    }

    /** 触发监听器 **/
    private void notifyChange() {
        listeners.forEach(StorageChangeListener::notifyChange);
    }

    /** 资源数据容器 **/
    private final class InnerValueHolder {

        /** 主数据存储空间 **/
        private Map<K, V> values;
        /** 索引数据存储空间 **/
        private Map<String, Map<Object, List<V>>> indexValues;
        /** 唯一索引数据存储空间 **/
        private Map<String, Map<Object, V>> uniqueIndexValues;


    }
}
