package com.li.gamecommon.resource.storage;

/**
 * 配表资源接口
 * @author li-yuanwen
 * @date 2022/1/24
 */
public interface ResourceStorage<I, E extends TableResource<I>> {


    /**
     * 获取配表资源
     * @param id 一行表的唯一标识
     * @return 配表对象
     */
    E getResource(I id);


}
