package com.li.common.resource.convertor.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.common.resource.convertor.ConvertorType;
import com.li.common.resource.convertor.StrConvertor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * json字符串转换器
 * @author li-yuanwen
 * @date 2022/3/26
 */
@Component
public class JsonStrConvertor implements StrConvertor {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public ConvertorType getType() {
        return ConvertorType.JSON;
    }

    @Override
    public Object convert(String content, TypeDescriptor targetDescriptor) {
        try {
            return objectMapper.readValue(content, targetDescriptor.getType());
        } catch (JsonProcessingException e) {
            String message = MessageFormatter.format("字符串[{}]无法转换成指定类型[{}]"
                    , content, targetDescriptor.getType()).getMessage();
            throw new RuntimeException(message);
        }
    }
}
