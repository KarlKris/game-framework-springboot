package com.li.core.dao.model;

/**
 * 数据状态
 * @author li-yuanwen
 * @date 2022/1/25
 */
public enum DataStatus {

    /**
     * 从db里面加载出来的数据，无需处理
     */
    INIT(0),

    /**
     * 对象被修改
     */
    MODIFY(1),

    /**
     * 需要删除，先删db以后再移除内存
     */
    DELETE(2),

    ;

    private int code;
    DataStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
