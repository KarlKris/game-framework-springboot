package com.li.gamecommon.resource.storage;

/**
 * 表的类所必须继承的接口
 * @author li-yuanwen
 * @date 2022/1/24
 */
public interface TableResource<K> extends ResourceValidate {

    /**
     * 一行表的唯一标识
     * @return 一行表的唯一标识
     */
    K getId();

}
