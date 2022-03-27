package com.li.gamecommon.resource.convertor;

import org.springframework.core.convert.TypeDescriptor;

/**
 * 字符串解析器
 * @author li-yuanwen
 * @date 2022/3/26
 */
public interface StrConvertor {

    TypeDescriptor STRING_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

    /**
     * 获取转换器类型
     * @return 转换器类型
     */
    ConvertorType getType();


    /**
     * 将字符串解析成指定类型对象
     * @param content 字符串
     * @param targetDescriptor 目标类型描述
     * @return 指定类型对象
     */
    Object convert(String content, TypeDescriptor targetDescriptor);

}
