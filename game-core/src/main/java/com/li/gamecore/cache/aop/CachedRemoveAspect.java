package com.li.gamecore.cache.aop;


import com.li.gamecore.cache.anno.CachedRemove;
import com.li.gamecore.cache.service.LocalCacheService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author li-yuanwen
 * 基于注解@CachedRemove的aop
 */
@Aspect
@Component
@Slf4j
public class CachedRemoveAspect {

    @Autowired
    private LocalCacheService localCacheService;


    @Pointcut(value = "@annotation(com.li.gamecore.cache.anno.CachedRemove)")
    public void cachedRemovePointcut() { }


    @After("cachedRemovePointcut()")
    public void afterInvoke(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        CachedRemove cachedRemove = AnnotationUtils.findAnnotation(method, CachedRemove.class);
        // todo 移除缓存

    }

}
