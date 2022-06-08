package com.li.common.resource.convertor.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.li.common.resource.convertor.ConvertorType;
import com.li.common.resource.convertor.StrConvertor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * json字符串转换器
 * @author li-yuanwen
 * @date 2022/3/26
 */
@Component
public class JsonStrConvertor implements StrConvertor {

    private final TypeFactory typeFactory = TypeFactory.defaultInstance();

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public ConvertorType getType() {
        return ConvertorType.JSON;
    }

    @Override
    public Object convert(String content, TypeDescriptor targetDescriptor) {
        try {
            JavaType javaType = null;
            if (targetDescriptor.isCollection()) {
                javaType = typeFactory.constructCollectionType( (Class<? extends Collection>) targetDescriptor.getType()
                        , targetDescriptor.getElementTypeDescriptor().getType());
            } else {
                javaType = typeFactory.constructType(targetDescriptor.getType());
            }
            return objectMapper.readValue(content, javaType);
        } catch (JsonProcessingException e) {
            String message = MessageFormatter.format("字符串[{}]无法转换成指定类型[{}]"
                    , content, targetDescriptor.getType()).getMessage();
            throw new RuntimeException(message, e);
        }
    }
}
