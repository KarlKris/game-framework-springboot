package com.li.gamecommon.resource.storage;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;

/**
 * StorageManagerFactoryBean
 * @author li-yuanwen
 * @date 2022/3/17
 */
public class StorageManagerFactoryBean implements FactoryBean<StorageManager>, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private StorageManager storageManager;

    @PostConstruct
    private void  init() {
        storageManager = applicationContext.getAutowireCapableBeanFactory().createBean(StorageManager.class);
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
