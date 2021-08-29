package com.li.gamemanager.common.entity;

/**
 * 涉及数据权限查询的类必须实现的接口
 * @author li-yuanwen
 * @date 2021/6/12 22:02
 **/
public interface BaseDataPermission {


    /**
     * 检查是否包含某字段名
     * @param fieldName 字段名
     * @return
     */
    boolean contain(String fieldName);

}
