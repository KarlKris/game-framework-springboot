package com.li.gamecommon.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author li-yuanwen
 * @date 2021/12/13
 */
public class ObjectsUtil {

    /** Object方法 **/
    public static final Set<Method> OBJECT_METHODS = new HashSet<>(Arrays.asList(Object.class.getDeclaredMethods()));

}
