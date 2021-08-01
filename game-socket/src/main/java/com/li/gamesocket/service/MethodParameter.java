package com.li.gamesocket.service;

import java.lang.reflect.Type;

/**
 * @author li-yuanwen
 * @date 2021/7/31 15:02
 * 方法参数接口
 **/
public interface MethodParameter {


    /**
     * 获取参数名
     * @return 参数名
     */
    String getParameterName();

    /**
     * 获取参数类型
     * @return 参数类型
     */
    Type getParameterType();

    /**
     * 是否是方法必须参数
     * @return true 参数不能为null
     */
    boolean isRequired();

    /**
     * 是否是身份标识参数
     * @return true 身份标识参数
     */
    boolean identity();

}
