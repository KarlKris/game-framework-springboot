package com.li.gamecore.dao;

/**
 * @author li-yuanwen
 * 数据库表对象基类
 */
public interface IEntity<PK> {

    /**
     * 获取主键
     * @return 主键
     */
    PK getId();
}
