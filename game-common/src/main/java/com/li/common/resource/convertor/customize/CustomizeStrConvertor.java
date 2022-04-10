package com.li.common.resource.convertor.customize;

import org.springframework.core.convert.TypeDescriptor;

/**
 * 自定义字符串解析器
 * @author li-yuanwen
 * @date 2022/3/26
 */
public interface CustomizeStrConvertor {


    /**
     * 判断能否转换成指定类型
     * @param targetDescriptor 指定类型描述信息
     * @return true 能转换
     */
    boolean canConvert(TypeDescriptor targetDescriptor);

    /**
     * 将字符串解析成指定类型
     * @param content 字符串
     * @param targetDescriptor 定类型描述信息
     * @return 解析结果
     */
    Object convert(String content, TypeDescriptor targetDescriptor);


}
