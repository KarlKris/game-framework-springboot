package com.li.gamecore.dao.core;

import java.util.List;

/**
 * @author li-yuanwen
 * 数据库SQL查询器接口
 */
public interface DataQuerier {


    /**
     * 查询数据库某表全部数据
     * @param entityClass 表对象
     * @param <E> 实际类型
     * @return 全部数据
     */
    <E> List<E> all(Class<E> entityClass);


    /**
     * 查询某表某个字段
     * @param entityClass 表对象
     * @param returnClass 字段对象
     * @param queryName 查询名
     * @param params 查询参数
     * @param <E> 表类型
     * @param <T> 字段类型
     * @return 字段的全部数据
     */
    <E, T> List<T> query(Class<E> entityClass, Class<T> returnClass, String queryName, Object... params);

    /**
     * 查询某表某行某个字段
     * @param entityClass 表对象
     * @param returnClass 字段对象
     * @param queryName 查询名
     * @param params 查询参数
     * @param <E> 表类型
     * @param <T> 字段类型
     * @return 某个字段
     */
    <E, T> T uniqueQuery(Class<E> entityClass, Class<T> returnClass, String queryName, Object... params);

}
