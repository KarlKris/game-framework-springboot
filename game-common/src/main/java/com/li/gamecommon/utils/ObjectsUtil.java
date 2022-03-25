package com.li.gamecommon.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.helpers.MessageFormatter;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author li-yuanwen
 * @date 2021/12/13
 */
public class ObjectsUtil {

    /** Object方法 **/
    public static final Set<Method> OBJECT_METHODS = new HashSet<>(Arrays.asList(Object.class.getDeclaredMethods()));

    public static String toJsonStr(ObjectMapper objectMapper, Object obj) throws JsonProcessingException {
        if (obj instanceof String) {
            return (String) obj;
        }
        return objectMapper.writeValueAsString(obj);
    }

    public static boolean isMap(Class<?> clz) {
        return Map.class.isAssignableFrom(clz);
    }

    public static boolean isCollection(Class<?> clz) {
        return Collection.class.isAssignableFrom(clz);
    }

    public static <E> E newInstance(Class<E> clz) {
        try {
            return clz.newInstance();
        } catch (Exception e) {
            String message = MessageFormatter.format("类[{}]没有无参构造函数", clz).getMessage();
            throw new RuntimeException(message);
        }
    }

}
