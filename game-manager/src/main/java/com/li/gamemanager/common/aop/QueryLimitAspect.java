package com.li.gamemanager.common.aop;

import com.xaweb.manager.common.entity.DataPermission;
import com.xaweb.manager.common.service.DataPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

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

    @Autowired
    private DataPermissionService dataPermissionService;

    /**
     * 定义切点
     */
    @Pointcut(value = "@annotation(com.xaweb.manager.common.aop.QueryLimit)")
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

        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        QueryLimit queryLimit = method.getAnnotation(QueryLimit.class);

        Class<?> entityClass = queryLimit.entityClass();
        String userName = queryLimit.userName();

        log.debug("[{}]进入@QueryLimit环绕注解AOP,限制实体[{}]", signature.getName(), entityClass.getSimpleName());

        for (Object arg : jp.getArgs()) {
            if (arg instanceof Criteria) {
                Criteria criteria = (Criteria) arg;
                for (DataPermission dataPermission : dataPermissionService.findByUser(userName)) {
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
            }
        }

        jp.proceed();
    }

}
