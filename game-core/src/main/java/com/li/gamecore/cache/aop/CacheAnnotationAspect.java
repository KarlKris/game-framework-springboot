package com.li.gamecore.cache.aop;


import com.li.gamecore.cache.anno.CachedRemove;
import com.li.gamecore.cache.core.Cache;
import com.li.gamecore.cache.core.CacheManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author li-yuanwen
 * 基于注解@CachedRemove的aop
 */
@Aspect
@Component
@Slf4j
public class CacheAnnotationAspect {

    public static final String SPEL_PREFIX = "#";

    private final ExpressionParser parser = new SpelExpressionParser();
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Autowired
    private CacheManager cacheManager;

    /** 缓存移除 **/
    @Pointcut(value = "@annotation(com.li.gamecore.cache.anno.CachedRemove)")
    public void cachedRemovePointcut() {
    }

    /** 缓存更新 **/
    @Pointcut(value = "@annotation(com.li.gamecore.cache.anno.CachedPut)")
    public void cachedPutPointcut() {
    }

    /** 查询缓存 **/
    @Pointcut(value = "@annotation(com.li.gamecore.cache.anno.Cachedable)")
    public void cachedablePointcut() {
    }

    /** 缓存移除 **/
    @After("cachedRemovePointcut()")
    public void afterInvoke(JoinPoint jp) throws NoSuchMethodException {
        Method targetMethod = getTargetMethod(jp);
        CachedRemove cachedRemove = AnnotationUtils.findAnnotation(targetMethod, CachedRemove.class);

        String cacheName = cachedRemove.name();
        EvaluationContext evaluationContext = null;
        if (cacheName.startsWith(SPEL_PREFIX)) {
            Expression expression = parser.parseExpression(cacheName);
            Object[] args = jp.getArgs();
            evaluationContext = bindParam(targetMethod, args);
            cacheName = expression.getValue(evaluationContext).toString();
        }


        // 移除缓存
        Cache cache = cacheManager.getCache(cachedRemove.type(), cacheName);
        if (cache != null) {
            String key = cachedRemove.key();
            if (key.startsWith(SPEL_PREFIX)) {
                Expression expression = parser.parseExpression(key);
                if (evaluationContext == null) {
                    Object[] args = jp.getArgs();
                    evaluationContext = bindParam(targetMethod, args);
                }
                key = expression.getValue(evaluationContext).toString();
            }
            cache.remove(key);
        }


    }


    /**
     * 将方法的参数名和参数值绑定
     *
     * @param method 方法，根据方法获取参数名
     * @param args   方法的参数值
     * @return
     */
    private EvaluationContext bindParam(Method method, Object[] args) {
        //获取方法的参数名
        String[] params = discoverer.getParameterNames(method);

        //将参数名与参数值对应起来
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], args[len]);
        }
        return context;
    }

    /**
     * 获取当前执行的方法
     *
     * @param pjp
     * @return
     * @throws NoSuchMethodException
     */
    private Method getTargetMethod(JoinPoint pjp) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        return pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
    }

}
