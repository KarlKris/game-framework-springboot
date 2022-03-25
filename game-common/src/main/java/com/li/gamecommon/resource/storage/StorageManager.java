package com.li.gamecommon.resource.storage;

import com.li.gamecommon.resource.anno.ResourceObj;
import com.li.gamecommon.resource.core.ResourceDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

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

    public void validate(ResourceDefinition definition) {
        getResourceStorage(definition.getClz()).validate();
    }


    public ResourceStorage<?, ?> getResourceStorage(Class<?> clz) {
        ResourceStorage<?, ?> storage = storages.get(clz);
        if (storage != null) {
            return storage;
        }

        throw new RuntimeException("资源[" + clz.getName() + "]ResourceStorage不存在");
    }

    private void initializeStorage(Class<?> clz) {
        ResourceDefinition definition = this.resourceDefinitions.get(clz);
        if (definition == null) {
            throw new RuntimeException("资源类[" + clz.getName() + "]的ResourceDefinition不存在");
        }
        ResourceObj obj = AnnotationUtils.findAnnotation(clz, ResourceObj.class);
        assert obj != null;
        DefaultResourceStorage<?, ?> storage = applicationContext.getAutowireCapableBeanFactory().createBean(DefaultResourceStorage.class);
        ResourceStorage<?, ?> prev = storages.putIfAbsent(clz, storage);
        if (prev == null) {
            storage.initialize(definition, applicationContext.getBean(obj.reader()));
            return;
        }
        applicationContext.getAutowireCapableBeanFactory().destroyBean(storage);
    }
}
