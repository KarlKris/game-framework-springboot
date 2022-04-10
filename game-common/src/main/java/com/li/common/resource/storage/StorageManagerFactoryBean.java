package com.li.common.resource.storage;

import com.li.common.resource.core.ResourceDefinition;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * StorageManagerFactoryBean
 * @author li-yuanwen
 * @date 2022/3/17
 */
@Slf4j
public class StorageManagerFactoryBean implements FactoryBean<StorageManager>, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private StorageManager storageManager;

    private List<ResourceDefinition> definitions;

    public void setDefinitions(List<ResourceDefinition> definitions) {
        this.definitions = definitions;
    }

    @PostConstruct
    private void init() {
        storageManager = applicationContext.getAutowireCapableBeanFactory().createBean(StorageManager.class);
        long start = System.currentTimeMillis();
        initializeStorages();
        storageValidate();
        long end = System.currentTimeMillis();

        if (log.isInfoEnabled()) {
            log.info("加载并验证资源表 数量:{}, 耗时:{}毫秒", definitions.size(), (end - start));
        }
    }

    /** 初始化Storage **/
    private void initializeStorages() {
        // 加载失败的资源
        StringBuilder stringBuilder = new StringBuilder();
        for (ResourceDefinition definition : definitions) {
            try {
                storageManager.initialize(definition);
            } catch (Exception e) {
                stringBuilder.append(definition.getClz().getName()).append(",");
                e.printStackTrace();
            }
        }
        if (stringBuilder.length() > 0) {
            String message = MessageFormatter.format("加载资源[{}]失败", stringBuilder).getMessage();
            throw new RuntimeException(message);
        }
    }

    /** 验证 **/
    private void storageValidate() {
        // 验证失败的资源
        StringBuilder stringBuilder = new StringBuilder();
        for (ResourceDefinition definition : definitions) {
            try {
                storageManager.validate(definition);
            } catch (Exception e) {
                stringBuilder.append(definition.getClz().getName()).append(",");
                e.printStackTrace();
            }
        }
        if (stringBuilder.length() > 0) {
            String message = MessageFormatter.format("验证资源[{}]失败", stringBuilder).getMessage();
            throw new RuntimeException(message);
        }
    }



    @Override
    public StorageManager getObject() throws Exception {
        return storageManager;
    }

    @Override
    public Class<?> getObjectType() {
        return StorageManager.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
