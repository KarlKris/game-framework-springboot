package com.li.gamecommon.resource.storage;

import com.li.gamecommon.resource.core.ResourceDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 资源容器
 * @author li-yuanwen
 * @date 2022/3/16
 */
@Slf4j
public class StorageManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /** 资源对象信息容器 **/
    private final ConcurrentHashMap<Class<?>, ResourceDefinition> resourceDefinitions = new ConcurrentHashMap<>();
    /** 资源工厂容器 **/
    private final ConcurrentHashMap<Class<?>, ResourceStorage<?, ?>> storages = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void initialize(ResourceDefinition definition) {
        Class<?> clz = definition.getClz();
        if (resourceDefinitions.putIfAbsent(clz, definition) != null) {
            throw new RuntimeException("资源类[" + clz.getName() + "]ResourceDefinition重复");
        }
        initializeStorage(clz);
    }


    public ResourceStorage<?, ?> getResourceStorage(Class<?> clz) {
        ResourceStorage<?, ?> storage = storages.get(clz);
        if (storage != null) {
            return storage;
        }

        ResourceDefinition definition = resourceDefinitions.get(clz);
        if (definition != null) {
            return initializeStorage(clz);
        }

        throw new RuntimeException("资源[" + clz.getName() + "]ResourceStorage不存在");
    }

    private ResourceStorage<?, ?> initializeStorage(Class<?> clz) {
        return null;
    }
}
