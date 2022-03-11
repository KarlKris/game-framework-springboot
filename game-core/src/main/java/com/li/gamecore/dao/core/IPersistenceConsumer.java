package com.li.gamecore.dao.core;

import com.li.gamecore.dao.AbstractEntity;

import java.io.Serializable;

/**
 * 持久化队列消费器
 * @author li-yuanwen
 * @date 2022/3/10
 */
public interface IPersistenceConsumer extends Runnable {


    /**
     * 从待持久化队列中查找指定主键和指定类型的实体数据
     * @param id 主键
     * @param tClass 实体数据
     * @param <PK> 主键类型
     * @param <T> 实体数据实际类型
     * @return null or　对应的实体数据
     */
    <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T findById(PK id, Class<T> tClass);


    /**
     * 向消费器提交需持久化的实体
     * @param entity 实体数据
     */
    <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> void commit(T entity);

}
