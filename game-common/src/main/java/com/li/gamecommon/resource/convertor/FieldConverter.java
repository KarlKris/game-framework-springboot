package com.li.gamecommon.resource.convertor;

/**
 * 属性类型转换器
 * @author li-yuanwen
 * @date 2022/3/17
 */
public interface FieldConverter {


    /**
     * 将字符串传换成指定的类型
     * @param content
     * @return
     */
    Object convert(String content);

}
