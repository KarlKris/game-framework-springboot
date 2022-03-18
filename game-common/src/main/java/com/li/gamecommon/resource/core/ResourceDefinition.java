package com.li.gamecommon.resource.core;

import lombok.Getter;

/**
 * 资源定义信息
 * @author li-yuanwen
 * @date 2022/3/16
 */
@Getter
public class ResourceDefinition {

    /** 资源对象 **/
    private final Class<?> clz;
    /** 资源全路径 **/
    private final String location;

    public ResourceDefinition(Class<?> clz, String location) {
        this.clz = clz;
        this.location = location;
    }
}
