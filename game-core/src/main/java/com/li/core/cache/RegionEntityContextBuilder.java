package com.li.core.cache;

import com.li.core.dao.AbstractRegionEntity;

import java.io.Serializable;
import java.util.List;

/**
 * @author li-yuanwen
 * @date 2022/3/8
 */
public interface RegionEntityContextBuilder {


    /**
     * 区域缓存构建器
     * @param list 实体集
     * @param <PK> 主键类型
     * @param <FK> 外键类型
     * @param <T> 实体类型
     * @param <R> 区域缓存类型
     * @return 区域缓存
     */
    <PK extends Comparable<PK> & Serializable
            , FK extends Comparable<FK> & Serializable
            , T extends AbstractRegionEntity<PK, FK>
            , R extends RegionEntityContext<PK, FK, T>>  R build(List<T> list);

}
