package com.li.gamecore.dao.core;

import com.li.gamecore.dao.AbstractEntity;

/**
 * 持久化操作后的监听器
 * @author li-yuanwen
 * @date 2022/3/10
 */
public interface IPersistenceListener {

    /**
     * 通知
     * @param entity 持久化的实体数据
     * @param exception null or 持久化异常
     */
    void notify(AbstractEntity<?> entity, Exception exception);


}
