package com.li.gamecore.cache.config;

/**
 * @author li-yuanwen
 * 缓存类型
 */
public enum CachedType {

    /** 数据库实体 **/
    ENTITY,

    /** 数据库区域缓存实体 **/
    REGION_ENTITY,

    /** 本地缓存 **/
    LOCAL,

    /** 分布式缓存 **/
    REMOTE,

    ;


}
