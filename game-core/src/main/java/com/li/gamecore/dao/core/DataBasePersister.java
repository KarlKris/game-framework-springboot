package com.li.gamecore.dao.core;

import com.li.gamecore.dao.IEntity;
import com.li.gamecore.dao.model.PersistType;

/**
 * @author li-yuanwen
 * 持久化接口
 */
public interface DataBasePersister {


    /**
     * 立即回写
     * @param type 持久化类型
     * @param entity 回写实体
     * @return true 回写成功
     */
    boolean immediatePersist(PersistType type, IEntity entity);


    /**
     * 异步回写
     * @param type 持久化类型
     * @param entity 回写实体
     */
    void asynPersist(PersistType type, IEntity entity);


    /**
     * 异步回写实体
     * @param entity 回写实体
     */
    void asynPersist(IEntity entity);

}
