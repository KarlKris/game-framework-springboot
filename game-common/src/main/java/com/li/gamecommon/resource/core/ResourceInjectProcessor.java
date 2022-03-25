package com.li.gamecommon.resource.core;

import com.li.gamecommon.resource.anno.ResourceId;
import com.li.gamecommon.resource.anno.ResourceInject;
import com.li.gamecommon.resource.storage.ResourceStorage;
import com.li.gamecommon.resource.storage.StorageManager;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @ResourceInject 注解功能实现
 * @author li-yuanwen
 * @date 2022/3/23
 */
@Slf4j
@Component
public class ResourceInjectProcessor extends InstantiationAwareBeanPostProcessorAdapter {

    private final StorageManager storageManager;
    private final ConversionService conversionService;

    public ResourceInjectProcessor(@Autowired StorageManager storageManager
            , @Autowired ConversionService conversionService) {
        this.storageManager = storageManager;
        this.conversionService = conversionService;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            ResourceInject annotation = field.getAnnotation(ResourceInject.class);
            if (annotation == null) {
                return;
            }
            if (ResourceStorage.class.isAssignableFrom(field.getType())) {
                // 注入Storage
                injectStorage(bean, field);
            } else {
                // 注入实例
                injectInstance(bean, field, annotation);
            }
        });
        return super.postProcessAfterInstantiation(bean, beanName);
    }

    /**
     * 注入Storage实例
     * @param bean bean
     * @param field 注入属性目标
     */
    private void injectStorage(final Object bean, final Field field) {
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            String message = MessageFormatter.format("beanClass:{} field:{} 类型声明不正确"
                    , bean.getClass().getName(), field.getName()).getMessage();
            throw new IllegalArgumentException(message);
        }

        // 拿到泛型参数实际类型
        Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
        Class<?> clz;
        if (actualTypeArguments[1] instanceof Class) {
            clz = (Class<?>) actualTypeArguments[1];
        } else if (actualTypeArguments[1] instanceof ParameterizedType) {
            clz = (Class<?>) ((ParameterizedType) actualTypeArguments[1]).getRawType();
        } else {
            String message = MessageFormatter.format("beanClass:{} field:{} 类型声明不正确"
                    , bean.getClass().getName(), field.getName()).getMessage();
            throw new IllegalArgumentException(message);
        }

        inject(bean, field, getResourceStorage(clz));
    }

    /**
     * 注入Storage中的数据实例
     * @param bean bean
     * @param field 注入属性目标
     * @param annotation 注解数据
     */
    private void injectInstance(final Object bean , final Field field, final ResourceInject annotation) {
        // 资源类型
        Class<?> resourceClz = annotation.type();
        // 借用Spring ConversionService做类型转换
        // 因为注解中的key只能做简单的互转,也就是说key值不应该是复杂的，而应该是类型String,Integer这些基本类型
        // 注解中的key为String
        TypeDescriptor sourceTypeDescriptor = TypeDescriptor.valueOf(String.class);
        // 主键类型
        Field idField = getFirstDeclaredFieldWith(resourceClz, ResourceId.class);
        assert idField != null;
        TypeDescriptor targetTypeDescriptor = new TypeDescriptor(idField);

        Object key = conversionService.convert(annotation.key(), sourceTypeDescriptor, targetTypeDescriptor);

        @SuppressWarnings("rawtypes")
        ResourceStorage resourceStorage = getResourceStorage(resourceClz);
        // 增加监听器,在资源变更时同步变更实例
        resourceStorage.addListener(storage -> injectInstance0(bean, field, annotation, storage, resourceClz, key));

        // 注入实例
        injectInstance0(bean, field, annotation, resourceStorage, resourceClz, key);
    }

    /**
     * 注入实例
     * @param bean bean
     * @param field 注入属性目标
     * @param annotation 注解数据
     * @param storage 注入资源Storage
     * @param resourceClz 注入资源类型
     * @param key 注入资源id值
     */
    private void injectInstance0(Object bean, Field field, ResourceInject annotation
            , @SuppressWarnings("rawtypes") ResourceStorage storage
            , Class<?> resourceClz
            , Object key) {

        @SuppressWarnings("unchecked")
        final Object instance = storage.getResource(key);
        if (annotation.required() && instance == null) {
            String message = MessageFormatter.format("beanClass:{} 属性:{} 注入值不存在"
                    , bean.getClass().getName(), field.getName()).getMessage();
            throw new RuntimeException(message);
        }

        if (instance == null) {
            return;
        }

        final Class<?> type = field.getType();
        if (type.isInstance(instance)) {
            // 注入实例
            inject(bean, field, instance);
        } else {
            final String fieldName = annotation.field();
            if (StringUtils.isEmpty(fieldName)) {
                String message = MessageFormatter.format("beanClass:{} 属性:{} 注解field属性字段未定义"
                        , bean.getClass().getName(), field.getName()).getMessage();
                throw new RuntimeException(message);
            }

            ReflectionUtils.doWithFields(resourceClz, resourceField -> {
                if (!resourceField.getName().equals(fieldName)) {
                    return;
                }
                ReflectionUtils.makeAccessible(resourceField);
                // 值
                Object value = resourceField.get(instance);
                if (!type.isInstance(instance)) {
                    value = conversionService.convert(value, TypeDescriptor.valueOf(value.getClass()), new TypeDescriptor(field));
                }
                // 注入属性
                inject(bean, field, value);
            });
        }
    }


    private ResourceStorage<?, ?> getResourceStorage(Class<?> clz) {
        ResourceStorage<?, ?> storage = storageManager.getResourceStorage(clz);
        if (storage == null) {
            String message = MessageFormatter.format("资源类:{} 对应的资源文件不存在"
                    , clz.getName()).getMessage();
            throw new IllegalArgumentException(message);
        }
        return storage;
    }

    /** 属性注入 **/
    private void inject(Object bean, Field field, Object value) {
        ReflectionUtils.makeAccessible(field);
        try {
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            String message = MessageFormatter.format("beanClass:{} 属性:{} 注入失败"
                    , bean.getClass().getName(), field.getName()).getMessage();
            throw new RuntimeException(message);
        }
    }

    /** 返回类中第一个带有指定注解修饰的属性 or null **/
    private static Field getFirstDeclaredFieldWith(Class<?> clz, Class<? extends Annotation> annotation) {
        for (Field field : clz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation)) {
                return field;
            }
        }
        return null;
    }

}
