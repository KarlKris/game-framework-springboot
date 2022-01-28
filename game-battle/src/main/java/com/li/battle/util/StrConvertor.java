package com.li.battle.util;

/**
 * 表配置属性转换器
 * @author li-yuanwen
 */
public interface StrConvertor<T> {

    /**
     * 将字符串按规则转换成指定对象
     * @param str 字符串
     * @return 对象
     */
    T convert(String str);

}
