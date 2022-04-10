package com.li.common.resource.resolver;

import com.li.common.resource.anno.ResourceId;
import com.li.common.resource.anno.ResourceIndex;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 解析器工厂
 * @author li-yuanwen
 * @date 2022/3/22
 */
public class ResolverFactory {

    /**
     * 根据Class对象创建索引解析器集
     * @param clz Class对象
     * @return 索引解析器集
     */
    public static List<IndexResolver> createIndexResolvers(Class<?> clz) {
        List<IndexResolver> resolvers = new LinkedList<>();
        ReflectionUtils.doWithFields(clz, field -> {
            if (field.isAnnotationPresent(ResourceIndex.class)) {
                resolvers.add(new FieldIndexResolver(field));
            }
        });
        ReflectionUtils.doWithMethods(clz, method -> {
            if (method.isAnnotationPresent(ResourceIndex.class)) {
                resolvers.add(new MethodIndexResolver(method));
            }
        });
        return resolvers;
    }


    /**
     * 根据Class对象创建Id解析器
     * @param clz Class对象
     * @return Id解析器
     */
    public static Resolver createIdResolver(Class<?> clz) {
        final List<Field> idFields = new LinkedList<>();
        ReflectionUtils.doWithFields(clz, field -> {
            if (field.isAnnotationPresent(ResourceId.class)) {
                idFields.add(field);
            }
        });

        if (!idFields.isEmpty()) {
            if (idFields.size() > 1) {
                String message = MessageFormatter.format("类:{}的主键标识注解修饰属性重复", clz.getName()).getMessage();
                throw new RuntimeException(message);
            } else  {
                Field field = idFields.get(0);
                return new FieldResolver(field);
            }
        }

        final List<Method> idMethods = new LinkedList<>();
        ReflectionUtils.doWithMethods(clz, method -> {
            if (method.isAnnotationPresent(ResourceId.class)) {
                idMethods.add(method);
            }
        });

        if (!idMethods.isEmpty()) {
            if (idMethods.size() > 1) {
                String message = MessageFormatter.format("类:{}的主键标识注解修饰方法重复", clz.getName()).getMessage();
                throw new RuntimeException(message);
            } else  {
                Method method = idMethods.get(0);
                return new MethodResolver(method);
            }
        }

        String message = MessageFormatter.format("类:{}的主键标识注解缺失", clz.getName()).getMessage();
        throw new RuntimeException(message);
    }


    /**
     * 创建基于Field的解析器
     * @param field field
     * @return 解析器
     */
    public static Resolver createFieldResolver(Field field) {
        return new FieldResolver(field);
    }


}
