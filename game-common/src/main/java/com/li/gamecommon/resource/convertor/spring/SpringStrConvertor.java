package com.li.gamecommon.resource.convertor.spring;

import com.li.gamecommon.resource.convertor.ConvertorType;
import com.li.gamecommon.resource.convertor.StrConvertor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 利用Spring自带的ConversionService来进行转换
 * @author li-yuanwen
 * @date 2022/3/26
 */
@Component
@ConditionalOnClass(ConversionService.class)
public class SpringStrConvertor implements StrConvertor {

    @Resource
    private ConversionService conversionService;

    @Override
    public ConvertorType getType() {
        return ConvertorType.SPRING;
    }

    @Override
    public Object convert(String content, TypeDescriptor targetDescriptor) {
        return conversionService.convert(content, STRING_DESCRIPTOR, targetDescriptor);
    }
}
