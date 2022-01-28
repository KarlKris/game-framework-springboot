package com.li.gamecore.dao.core;

import com.li.gamecore.dao.AbstractEntity;

import java.io.Serializable;

/**
 * 数据持久化接口
 * @author li-yuanwen
 * @date 2022/1/25
 */
public interface DataPersistence {


    /**
     * 回写到数据库
     * @param entity 回写内容
     * @param <PK> /
     */
    <PK extends Comparable<PK> & Serializable> void commit(AbstractEntity<PK> entity);


}
