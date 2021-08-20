package com.li.gamecore.dao.service;

import com.li.gamecore.dao.EntityBuilder;
import com.li.gamecore.dao.IEntity;

import java.io.Serializable;

/**
 * @author li-yuanwen
 * 数据库实体对外接口
 */
public interface EntityService {


    /**
     * 读取数据库表数据实体
     * @param id 标识
     * @param tClass 实体类型Class
     * @param <PK> 标识类型
     * @param <T> 实体类型
     * @return 数据库实体
     */
    <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> T load(PK id, Class<T> tClass);


    /**
     * 读取数据库表数据实体,若没有对应实体则根据实体构造器创建,并写入数据库
     * @param id 标识
     * @param tClass 实体类型Class
     * @param entityBuilder 实体构造器
     * @param <PK> 标识类型
     * @param <T> 实体类型
     * @return 数据库实体
     */
    <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> T loadOrCreate(PK id, Class<T> tClass
            , EntityBuilder<PK, T> entityBuilder);


    /**
     * 创建数据库表数据
     * @param entity 创建实体
     * @param <PK> 标识类型
     * @param <T> 实体类型
     * @return 数据库实体
     */
    <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> T  create(T entity);


}
