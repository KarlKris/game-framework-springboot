package com.li.gamecommon.resource.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源实例Storage注入
 * @author li-yuanwen
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StorageInject {

    /**
     * 标识值
     * @return 资源实例标识值
     */
    String key() default "";

    /**
     * 资源类型
     * @return 资源类型
     */
    Class<?> type() default Void.class;

    /**
     * 注入资源的属性名
     * @return 注入资源的属性名
     */
    String field() default "content";

    /**
     * 注入值是否必须
     * @return true 必须存在资源
     */
    boolean required() default true;

}
