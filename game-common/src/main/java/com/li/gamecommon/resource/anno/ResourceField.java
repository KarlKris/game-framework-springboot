package com.li.gamecommon.resource.anno;

import com.li.gamecommon.resource.convertor.ConvertorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源表属性注解
 * @author li-yuanwen
 * @date 2022/3/26
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceField {

    /**
     * 属性解析使用的类型转换器
     * @return 类型转换器
     */
    ConvertorType convertorType() default ConvertorType.SPRING;

}
