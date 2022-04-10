package com.li.engine.service.push;

import com.li.engine.anno.InnerPushInject;
import com.li.engine.anno.OuterPushInject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 * @date 2021/8/7 09:47
 * 注解@InnerPushInject @OuterPushInject注入
 **/
@Component
@Order
public class PushInjectProcessor implements SmartInstantiationAwareBeanPostProcessor {

    @Resource
    private PushManager pushManager;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            InnerPushInject innerPushInject = field.getAnnotation(InnerPushInject.class);
            if (innerPushInject != null) {
                Object pushProxy = pushManager.getInnerPushProxy(field.getType());
                field.setAccessible(true);
                field.set(bean, pushProxy);
            }

            OuterPushInject outerPushInject = field.getAnnotation(OuterPushInject.class);
            if (outerPushInject != null) {
                Object pushProxy = pushManager.getOuterPushProxy(field.getType());
                field.setAccessible(true);
                field.set(bean, pushProxy);
            }


        });

        return bean;
    }
}
