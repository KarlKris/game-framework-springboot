package com.li.gamemanager.common.aop;

import com.li.gamemanager.common.entity.DataPermission;
import com.li.gamemanager.common.service.DataPermissionReactiveService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author li-yuanwen
 * <p>
 * QueryLimit注解AOP
 */
@Slf4j
@Aspect
@Component
public class QueryLimitAspect {

    public static final String SPEL_PREFIX = "#";

    private final ExpressionParser parser = new SpelExpressionParser();
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Autowired
    private DataPermissionReactiveService dataPermissionReactiveService;

    /**
     * 定义切点
     */
    @Pointcut(value = "@annotation(com.li.gamemanager.common.aop.QueryLimit)")
    public void queryLimitPointCut() {
    }

    ;

    /**
     * 环绕通知
     *
     * @param jp
     */
    @Around("queryLimitPointCut()")
    public void queryLimitAround(ProceedingJoinPoint jp) throws Throwable {

        Method targetMethod = getTargetMethod(jp);
        QueryLimit queryLimit = AnnotationUtils.findAnnotation(targetMethod, QueryLimit.class);

        Class<?> entityClass = queryLimit.entityClass();
        String userName = queryLimit.userName();

        EvaluationContext evaluationContext = null;
        if (userName.startsWith(SPEL_PREFIX)) {
            Expression expression = parser.parseExpression(userName);
            Object[] args = jp.getArgs();
            evaluationContext = bindParam(targetMethod, args);
            userName = expression.getValue(evaluationContext, String.class);
        }

        log.debug("[{}]进入@QueryLimit环绕注解AOP,限制实体[{}]", targetMethod.getName(), entityClass.getSimpleName());

        for (Object arg : jp.getArgs()) {
            if (arg instanceof Criteria) {
                Criteria criteria = (Criteria) arg;
                dataPermissionReactiveService.findByUser(userName).flatMap(dataPermissions -> {
                    for (DataPermission dataPermission : dataPermissions) {
                        if (!dataPermission.getEntityName().equals(entityClass.getSimpleName())) {
                            continue;
                        }
                        for (Map.Entry<String, Object> entry : dataPermission.getLimit().entrySet()) {
                            String fieldName = entry.getKey();
                            Field field = ReflectionUtils.findField(entityClass, fieldName);
                            if (field == null) {
                                log.warn("实体[{}]不存在字段名[{}],请检查数据权限[{}]实体内容", entityClass.getSimpleName()
                                        , fieldName, dataPermission.getId());
                                continue;
                            }
                            Criteria temp = Criteria.where(entry.getKey());
                            Object value = entry.getValue();
                            if (value instanceof Collection) {
                                temp.in((Collection) value);
                            } else {
                                temp.is(value);
                            }
                            criteria.andOperator(temp);
                        }
                    }
                    try {
                        jp.proceed();
                        return Mono.empty();
                    } catch (Throwable throwable) {
                        log.error("执行目标方法出现未知异常", throwable);
                        return Mono.error(throwable);
                    }
                });
            }
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

}
