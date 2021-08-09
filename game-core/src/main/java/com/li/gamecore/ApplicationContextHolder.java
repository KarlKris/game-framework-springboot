package com.li.gamecore;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * ApplicationContext持有对象工具类
 */
@Component
@Order(1)
public class ApplicationContextHolder implements ApplicationContextAware {

    protected static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /** 获取Bean **/
    public static <T> T getBean(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }

    /** 获取Bean **/
    public static <T> T getBean(String beanName, Class<T> tClass) {
        return applicationContext.getBean(beanName, tClass);
    }

}
