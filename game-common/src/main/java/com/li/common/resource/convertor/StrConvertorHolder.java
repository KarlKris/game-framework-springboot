package com.li.common.resource.convertor;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 字符串转换器持有类
 * @author li-yuanwen
 * @date 2022/3/26
 */
@Component
public class StrConvertorHolder {

    private final Map<ConvertorType, StrConvertor> strConvertorHolder;

    public StrConvertorHolder(List<StrConvertor> convertors) {
        Map<ConvertorType, StrConvertor> strConvertorHolder = new EnumMap<>(ConvertorType.class);
        for (StrConvertor convertor : convertors) {
            if (strConvertorHolder.putIfAbsent(convertor.getType(), convertor) != null) {
                String message = MessageFormatter.format("转换器类型[{}]重复", convertor.getType()).getMessage();
                throw new BeanInitializationException(message);
            }
        }
        this.strConvertorHolder = strConvertorHolder;
    }

    public StrConvertor getStrConvertorByType(ConvertorType type) {
        return strConvertorHolder.get(type);
    }

}
