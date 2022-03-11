package com.li.gamecore.cache.model;

import com.li.gamecore.dao.AbstractRegionEntity;

import java.io.Serializable;
import java.util.Map;

/**
 * 区域实体缓存容器基类,实际进入缓存的对象
 * @author li-yuanwen
 * @date 2022/3/8
 */
public abstract class RegionEntityContext<PK extends Comparable<PK> & Serializable
        , FK extends Comparable<FK> & Serializable, T extends AbstractRegionEntity<PK, FK>> {


    private final Map<PK, T> cache;

    public RegionEntityContext(Map<PK, T> cache) {
        this.cache = cache;
    }

    /**
     * 获取单个实体对象
     * @param id 主键id
     * @return 实体对象
     */
    public T findById(PK id) {
        return cache.get(id);
    }

    /**
     * 添加单个实体
     * @param entity 实体对象
     */
    public void addEntity(T entity) {
        this.cache.put(entity.getId(), entity);
    }

    /**
     * 删除单个实体
     * @param entity 实体对象
     */
    public void remove(T entity) {
        this.cache.remove(entity.getId());
    }

}
