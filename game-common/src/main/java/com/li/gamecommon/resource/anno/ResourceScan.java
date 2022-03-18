package com.li.gamecommon.resource.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源扫描
 * @author li-yuanwen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceScan {

    /**
     * @return 资源类路径
     */
    String[] value();

    /**
     * @return 资源表根路径
     */
    String rootPath();

}
