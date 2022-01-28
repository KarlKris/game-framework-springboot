package com.li.gamecore.dao.core;

import com.li.gamecore.dao.AbstractEntity;
import com.li.gamecore.dao.IEntity;

import java.io.Serializable;

/**
 * 数据库访问接口
 * @author li-yuanwen
 */
public interface DataAccessor {

    /**
     * 读取数据库表数据
     * @param id 主键id
     * @param tClass 实体对象class
     * @param <PK> 主键
     * @param <T> 实体类型
     * @return 数据库表某行数据对应实体对象
     */
    <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> T load(PK id, Class<T> tClass);

    /**
     * 移除数据库表数据
     * @param entity 需要移除的实体
     */
     void remove(AbstractEntity<?> entity);

    /**
     * 更新数据库表数据
     * @param entity 需要更新的实体
     */
    void update(AbstractEntity<?> entity);

    /**
     * 创建数据库表数据
     * @param entity 新创建的实体
     * @param <PK> 主键
     * @param <T> 实体
     */
    void create(AbstractEntity<?> entity);
}
