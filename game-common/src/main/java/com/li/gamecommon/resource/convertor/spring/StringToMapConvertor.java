package com.li.gamecommon.resource.convertor.spring;

import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 解析字符串成Map对象 key1:value1,key2:value2
 * @author li-yuanwen
 * @date 2022/3/27
 */
public class StringToMapConvertor implements ConditionalGenericConverter {


    private final ConversionService conversionService;

    public StringToMapConvertor(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        TypeDescriptor mapKeyTypeDescriptor = targetType.getMapKeyTypeDescriptor();
        boolean keyMatch = mapKeyTypeDescriptor == null || this.conversionService.canConvert(sourceType, mapKeyTypeDescriptor);
        if (!keyMatch) {
            return false;
        }
        return targetType.getMapValueTypeDescriptor() == null
                || this.conversionService.canConvert(sourceType, targetType.getMapValueTypeDescriptor());
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Map.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        String string = (String) source;

        String[] fields = StringUtils.commaDelimitedListToStringArray(string);

        TypeDescriptor mapKeyTypeDescriptor = targetType.getMapKeyTypeDescriptor();
        TypeDescriptor mapValueTypeDescriptor = targetType.getMapValueTypeDescriptor();
        Map<Object, Object> map = CollectionFactory.createMap(targetType.getType()
                , mapKeyTypeDescriptor == null ? null : mapKeyTypeDescriptor.getType(), fields.length);
        for (String field : fields) {
            String[] keyValue = StringUtils.delimitedListToStringArray(field, ":");
            Object key = convertKeyValue(keyValue[0], sourceType, mapKeyTypeDescriptor);
            Object value = convertKeyValue(keyValue[1], sourceType, mapValueTypeDescriptor);
            map.put(key, value);
        }
        return map;
    }

    private Object convertKeyValue(Object source, TypeDescriptor sourceType, @Nullable TypeDescriptor targetType) {
        if (targetType == null) {
            return source;
        }
        return this.conversionService.convert(source, sourceType, targetType);
    }

}
