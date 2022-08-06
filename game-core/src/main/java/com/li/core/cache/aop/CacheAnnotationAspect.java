package com.li.core.cache.aop;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.common.utils.ObjectUtils;
import com.li.core.cache.anno.CachedPut;
import com.li.core.cache.anno.CachedEvict;
import com.li.core.cache.anno.Cachedable;
import com.li.core.cache.core.cache.Cache;
import com.li.core.cache.core.manager.CacheManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * 缓存注解AOP
 * @author li-yuanwen
 */
@Aspect
@Component
@Slf4j
public class CacheAnnotationAspect {

    public static final String SPEL_PREFIX = "#";

    private final ExpressionParser parser = new SpelExpressionParser();
    private final TemplateParserContext parserContext = new TemplateParserContext();
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Resource
    private CacheManager cacheManager;
    @Resource
    private ObjectMapper objectMapper;

    /** 缓存移除 **/
    @Pointcut(value = "@annotation(com.li.core.cache.anno.CachedEvict)")
    public void cachedRemovePointcut() {
    }

    /** 缓存更新 **/
    @Pointcut(value = "@annotation(com.li.core.cache.anno.CachedPut)")
    public void cachedPutPointcut() {
    }

    /** 查询缓存 **/
    @Pointcut(value = "@annotation(com.li.core.cache.anno.Cachedable)")
    public void cachedablePointcut() {
    }

    /** 缓存移除 **/
    @Before("cachedRemovePointcut()")
    public void beforeRemoveInvoke(JoinPoint jp) throws NoSuchMethodException, JsonProcessingException {
        
        Method targetMethod = getTargetMethod(jp);
        CachedEvict cachedEvict = AnnotationUtils.findAnnotation(targetMethod, CachedEvict.class);

        String cacheName = cachedEvict.name();
        EvaluationContext evaluationContext = null;
        if (cacheName.startsWith(SPEL_PREFIX)) {
            Object[] args = jp.getArgs();
            evaluationContext = bindParam(targetMethod, args);
            cacheName = getSpElValue(cacheName, evaluationContext);
        }

        if (log.isInfoEnabled()) {
            log.info("被注解@CachedEvict修饰方法:{}, 缓存名称:{} 执行前", targetMethod.getName(), cacheName);
        }

        // 移除缓存
        Cache cache = cacheManager.getCache(cachedEvict.type(), cacheName);
        if (cache != null) {
            String keySpEl = cachedEvict.key();
            String key = keySpEl;
            if (keySpEl.startsWith(SPEL_PREFIX)) {
                if (evaluationContext == null) {
                    Object[] args = jp.getArgs();
                    evaluationContext = bindParam(targetMethod, args);
                }
                key = getSpElValue(keySpEl, evaluationContext);
            }
            cache.remove(key);
        }
    }

    /** 缓存更新 **/
    @AfterReturning(value = "cachedPutPointcut()", returning = "result")
    public void afterPutInvoke(JoinPoint jp, Object result) throws NoSuchMethodException, JsonProcessingException {
        Method targetMethod = getTargetMethod(jp);
        CachedPut cachedPut = AnnotationUtils.findAnnotation(targetMethod, CachedPut.class);

        String cacheName = cachedPut.name();
        EvaluationContext evaluationContext = null;
        if (cacheName.startsWith(SPEL_PREFIX)) {
            Object[] args = jp.getArgs();
            evaluationContext = bindParam(targetMethod, args);
            cacheName = getSpElValue(cacheName, evaluationContext);
        }

        if (log.isInfoEnabled()) {
            log.info("被注解@CachedPut修饰方法:{}, 缓存名称:{} 执行后", targetMethod.getName(), cacheName);
        }

        Cache cache = cacheManager.getCache(cachedPut.type(), cacheName);
        if (cache == null) {
            cache = cacheManager.createCache(cachedPut.type(), cacheName, cachedPut.maximum(), cachedPut.expire());
        }

        String keySpEl = cachedPut.key();
        String key = keySpEl;
        if (keySpEl.startsWith(SPEL_PREFIX)) {
            if (evaluationContext == null) {
                Object[] args = jp.getArgs();
                evaluationContext = bindParam(targetMethod, args);
            }
            key = getSpElValue(keySpEl, evaluationContext);
        }

        cache.put(key, result);


    }

    /** 查询缓存 **/
    @Around("cachedablePointcut()")
    public Object aroundInvoke(ProceedingJoinPoint joinPoint) throws NoSuchMethodException, JsonProcessingException {
        Method targetMethod = getTargetMethod(joinPoint);
        Cachedable cachedable = AnnotationUtils.findAnnotation(targetMethod, Cachedable.class);

        String cacheName = cachedable.name();
        EvaluationContext evaluationContext = null;
        if (cacheName.startsWith(SPEL_PREFIX)) {
            Object[] args = joinPoint.getArgs();
            evaluationContext = bindParam(targetMethod, args);
            cacheName = getSpElValue(cacheName, evaluationContext);
        }

        if (log.isInfoEnabled()) {
            log.info("被注解@Cachedable修饰方法:{}, 缓存名称:{} 执行前", targetMethod.getName(), cacheName);
        }

        String keySpEl = cachedable.key();
        String key = keySpEl;
        if (keySpEl.startsWith(SPEL_PREFIX)) {
            if (evaluationContext == null) {
                Object[] args = joinPoint.getArgs();
                evaluationContext = bindParam(targetMethod, args);
            }
            key = getSpElValue(keySpEl, evaluationContext);
        }

        Cache cache = cacheManager.getCache(cachedable.type(), cacheName);
        if (cache == null) {
            cache = cacheManager.createCache(cachedable.type(), cacheName, cachedable.maximum(), cachedable.expire());
        }

        Object result = null;
        Class<?> returnType = targetMethod.getReturnType();
        if ((result = cache.get(key, returnType)) == null) {
            try {

                if (log.isInfoEnabled()) {
                    log.info("被注解@Cachedable修饰方法:{}, 不存在缓存名称:{}, 执行方法", targetMethod.getName(), cacheName);
                }

                result = joinPoint.proceed();
                if (cachedable.nullCache() || result != null) {
                    cache.put(key, result);
                }
                return result;
            } catch (Throwable throwable) {
                log.error("执行方法[{}],方法参数[{}]出现未知异常", targetMethod.getName(), joinPoint.getArgs(), throwable);
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("被注解@Cachedable修饰方法:{}, 存在缓存名称:{}", targetMethod.getName(), cacheName);
            }
        }

        return result;
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
        if (params == null) {
            return context;
        }

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


    private String getSpElValue(String spEl, EvaluationContext evaluationContext) throws JsonProcessingException {
        Expression expression = parser.parseExpression(spEl);
        Object expressionValue = expression.getValue(evaluationContext);
        return ObjectUtils.toJsonStr(objectMapper, expressionValue);
    }
}
