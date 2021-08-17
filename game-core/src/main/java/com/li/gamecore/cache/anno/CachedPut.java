package com.li.gamecore.cache.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author li-yuanwen
 * 用于某个方法，每次被调用，此方法都执行，并把结果更新到PutCache配置的地方，一般用于缓存更新
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachedPut {
}
