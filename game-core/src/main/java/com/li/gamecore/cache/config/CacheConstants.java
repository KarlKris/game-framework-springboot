package com.li.gamecore.cache.config;

/**
 * @author li-yuanwen
 * 缓存常量
 */
public interface CacheConstants {

    // 缓存大小常量

    /** 默认大小 **/
    short DEFAULT_MAXIMUM = 1000;

    /** double **/
    short DOUBLE_DEFAULT_MAXIMUM = 2000;


    // 缓存失效时间常量

    /** 10分钟 **/
    short DEFAULT_EXPIRE = 10;

    /** 20分钟 **/
    short DOUBLE_DEFAULT_EXPIRE = 20;

}
