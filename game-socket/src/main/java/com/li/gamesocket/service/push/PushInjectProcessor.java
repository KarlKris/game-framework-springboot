package com.li.gamesocket.service.push;

import com.li.gamesocket.anno.PushInject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * @author li-yuanwen
 * @date 2021/8/7 09:47
 * 注解@PushInject注入
 **/
@Component
@Order
public class PushInjectProcessor extends InstantiationAwareBeanPostProcessorAdapter {

    @Autowired
    private PushManager pushManager;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            PushInject pushInject = field.getAnnotation(PushInject.class);
            if (pushInject == null) {
                return;
            }

            Object pushProxy = pushManager.getPushProxy(field.getType());
            field.setAccessible(true);
            field.set(bean, pushProxy);
        });

        return bean;
    }
}
