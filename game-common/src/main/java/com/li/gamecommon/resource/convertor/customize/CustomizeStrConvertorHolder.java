package com.li.gamecommon.resource.convertor.customize;

import com.li.gamecommon.resource.convertor.ConvertorType;
import com.li.gamecommon.resource.convertor.StrConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义字符串解析器
 * @author li-yuanwen
 * @date 2022/3/26
 */
@Component
public class CustomizeStrConvertorHolder implements StrConvertor {

    private final List<CustomizeStrConvertor> convertors;

    public CustomizeStrConvertorHolder(@Autowired List<CustomizeStrConvertor> convertors) {
        this.convertors = convertors;
    }

    @Override
    public ConvertorType getType() {
        return ConvertorType.CUSTOMIZE;
    }

    @Override
    public Object convert(String content, TypeDescriptor targetDescriptor) {
        CustomizeStrConvertor convertor = getConvertor(targetDescriptor);
        if (convertor == null) {
            throw new ConverterNotFoundException(STRING_DESCRIPTOR, targetDescriptor);
        }
        return convertor.convert(content, targetDescriptor);
    }

    private CustomizeStrConvertor getConvertor(TypeDescriptor targetDescriptor) {
        for (CustomizeStrConvertor convertor : convertors) {
            if (!convertor.canConvert(targetDescriptor)) {
                continue;
            }
            return convertor;
        }
        return null;
    }
}
