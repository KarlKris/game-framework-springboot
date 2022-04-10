package com.li.manager.common.model;

/**
 * 数据权限类型
 * @author li-yuanwen
 * @date 2021/6/12 21:47
 **/
public enum DataPermissionType {

    /** 渠道 **/
    CHANNEL("channel"),

    ;

    /** 筛选字段名 **/
    private String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    DataPermissionType(String fieldName) {
        this.fieldName = fieldName;
    }


}
