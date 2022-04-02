package com.li.gamecommon.resource.reader;

import com.li.gamecommon.resource.anno.ResourceField;
import com.li.gamecommon.resource.convertor.ConvertorType;
import com.li.gamecommon.resource.convertor.StrConvertor;
import com.li.gamecommon.utils.TypeDescriptorUtil;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

/**
 * 文件资源读取器
 * @author li-yuanwen
 * @date 2022/3/17
 */
public interface ResourceReader {

    /**
     * 读取的文件后缀名
     * @return 文件后缀
     */
    String getFileSuffix();

    /**
     * 资源读取
     * @param in 资源Input
     * @param clz 目标类型
     * @param <E> 实际类型
     * @return 资源集
     */
    <E> List<E> read(InputStream in, Class<E> clz);


    /** 抽象属性持有信息 **/
    abstract class AbstractFieldHolder {

        /** 属性 **/
        private final Field field;
        /** TypeDescriptor **/
        private final TypeDescriptor descriptor;
        /** 转换器 **/
        private final StrConvertor convertor;

        public AbstractFieldHolder(Field field, Function<ConvertorType, StrConvertor> convertorFunction) {
            this.field = field;
            this.descriptor = TypeDescriptorUtil.newInstance(field);
            ReflectionUtils.makeAccessible(field);
            ResourceField annotation = field.getAnnotation(ResourceField.class);
            if (annotation != null) {
                this.convertor = convertorFunction.apply(annotation.convertorType());
            } else {
                this.convertor = convertorFunction.apply(ConvertorType.SPRING);
            }
        }

        /** 获取属性名称 **/
        public String getFieldName() {
            return field.getName();
        }

        /** 属性实例注入 **/
        public void inject(Object instance, String content) {
            try {
                Object value = convertor.convert(content, descriptor);
                field.set(instance, value);
            } catch (ConverterNotFoundException e) {
                FormattingTuple message = MessageFormatter.format("静态资源[{}]属性[{}]的转换器不存在",
                        instance.getClass().getSimpleName(), field.getName());
                throw new IllegalStateException(message.getMessage(), e);
            } catch (IllegalAccessException e) {
                FormattingTuple message = MessageFormatter.format("静态资源[{}]属性[{}]注入失败", instance.getClass(), field);
                throw new IllegalStateException(message.getMessage(), e);
            }

        }

    }

}
