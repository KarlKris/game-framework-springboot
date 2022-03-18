package com.li.gamecommon.resource.anno;

import com.li.gamecommon.resource.reader.ResourceReader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源类标注注解
 * @author li-yuanwen
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceObj {

    /** 资源模块名 */
    String value() default "";

    /** 资源读取器 **/
    Class<? extends ResourceReader> reader();

}
