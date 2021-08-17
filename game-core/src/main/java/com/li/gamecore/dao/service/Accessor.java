package com.li.gamecore.dao.service;

import com.li.gamecore.dao.IEntity;

import java.io.Serializable;

/**
 * @author li-yuanwen
 * 数据库访问接口
 */
public interface Accessor {

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
     * @param <PK> 主键
     * @param <T>  实体
     */
    <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void remove(T entity);

    /**
     * 更新数据库表数据
     * @param entity 需要更新的实体
     * @param <PK> 主键
     * @param <T> 更新后的实体
     */
    <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void update(T entity);

    /**
     * 创建数据库表数据
     * @param entity 新创建的实体
     * @param <PK> 主键
     * @param <T> 实体
     */
    <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void create(T entity);
}
