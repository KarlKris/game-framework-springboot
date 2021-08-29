package com.li.gamemanager.common.aop;

import java.lang.annotation.*;


/**
 * 查询注解AOP
 * @author li-yuanwen
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface QueryLimit {

    /**
     * 查询实体名称
     * @return /
     */
    Class<?> entityClass();

    /**
     * 用户账号
     * @return /
     */
    String userName();

}
