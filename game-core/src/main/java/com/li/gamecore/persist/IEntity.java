package com.li.gamecore.persist;

import java.io.Serializable;

/**
 * @author li-yuanwen
 * 数据库表对象基类
 */
public interface IEntity<PK> extends Serializable {

    /**
     * 获取主键
     * @return 主键
     */
    PK getId();
}
