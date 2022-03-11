package com.li.gamecore.dao.javassist;

import com.li.gamecommon.exception.EnhanceException;
import com.li.gamecommon.utils.ObjectsUtil;
import com.li.gamecore.dao.AbstractEntity;
import com.li.gamecore.dao.anno.Commit;
import com.li.gamecore.dao.core.IDataPersistence;
import javassist.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

import static com.li.gamecore.dao.javassist.JavassistConstants.*;

/**
 * 基于Javassist的代理工厂
 * @author li-yuanwen
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "spring.datasource.url")
public class JavassistProxyFactory {

    private final ClassPool classPool = ClassPool.getDefault();
    /** 构造器缓存 **/
    private final ConcurrentHashMap<String, Constructor<? extends AbstractEntity>> constructorHolder = new ConcurrentHashMap<>();

    @Resource
    private IDataPersistence persistence;

    /**
     * 创建实体增强对象
     **/
    public <PK extends Serializable, T extends AbstractEntity<PK>> T transform(T entity) {
        Class<? extends AbstractEntity> entityClass = entity.getClass();
        Constructor<? extends AbstractEntity> constructor = constructorHolder.computeIfAbsent(entityClass.getName(), k -> {
            try {
                return buildEnhanceClass(entityClass).getConstructor(entityClass, IDataPersistence.class);
            } catch (NotFoundException | CannotCompileException | NoSuchMethodException | ClassNotFoundException e) {
                log.error("增强类[{}]出现未知异常", entityClass.getSimpleName(), e);
                throw new EnhanceException(entity, e);
            }
        });
        try {
            return (T) constructor.newInstance(entity, persistence);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("增强类[{}]出现未知异常", entityClass.getSimpleName(), e);
            throw new EnhanceException(entity, e);
        }
    }



    private Class buildEnhanceClass(Class<? extends AbstractEntity> tClass) throws NotFoundException, CannotCompileException, ClassNotFoundException {
        String className = tClass.getName();
        CtClass ctClass = classPool.makeClass(tClass.getCanonicalName() + ENHANCE_SUFFIX);
        CtClass superClass = classPool.get(className);
        ctClass.setSuperclass(superClass);

        // 增加持久化Field
        CtField persistField = new CtField(classPool.get(IDataPersistence.class.getName()), PERSISTER_FIELD, ctClass);
        persistField.setModifiers(Modifier.PRIVATE + Modifier.FINAL);
        ctClass.addField(persistField);
        // 增加实际实体
        CtField entityField = new CtField(superClass, ENTITY_FIELD, ctClass);
        entityField.setModifiers(Modifier.PRIVATE + Modifier.FINAL);
        ctClass.addField(entityField);

        // 增加构造器
        CtConstructor ctConstructor = new CtConstructor(toCtClassArray(tClass, IDataPersistence.class), ctClass);

        String constructorBody = "{ this." + ENTITY_FIELD + "=$1;" +
                "this." + PERSISTER_FIELD + "=$2;}";
        ctConstructor.setBody(constructorBody);
        ctConstructor.setModifiers(Modifier.PUBLIC);
        ctClass.addConstructor(ctConstructor);

        // 增强方法
        ReflectionUtils.doWithMethods(tClass, method -> {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers)) {
                return;
            }
            Commit commit = AnnotationUtils.findAnnotation(method, Commit.class);
            CtMethod ctMethod = null;
            try {
                if (commit == null) {
                    ctMethod = buildMethod(ctClass, method);
                }else {
                    ctMethod = buildEnhanceMethod(ctClass, method, commit);
                }
                ctClass.addMethod(ctMethod);
            } catch (NotFoundException | CannotCompileException e) {
                throw new IllegalArgumentException("增强实体[" + className + "]方法[" + method.getName() + "]出现未知异常", e);
            }

        }, method -> {
            if (ObjectsUtil.OBJECT_METHODS.contains(method)) {
                return false;
            }
            if (Modifier.isFinal(method.getModifiers()) || Modifier.isStatic(method.getModifiers())
                    || Modifier.isPrivate(method.getModifiers())) {
                return false;
            }
            return !method.isSynthetic();
        });

        return ctClass.toClass();
    }


    private CtMethod buildMethod(CtClass ctClass, Method method) throws NotFoundException, CannotCompileException {
        Class<?> returnType = method.getReturnType();
        String methodName = method.getName();
        CtMethod ctMethod = new CtMethod(classPool.get(returnType.getName())
                , methodName
                , toCtClassArray(method.getParameterTypes())
                , ctClass);
        ctMethod.setModifiers(method.getModifiers());

        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length != 0) {
            ctMethod.setExceptionTypes(toCtClassArray(method.getParameterTypes()));
        }

        if (returnType == void.class) {
            ctMethod.setBody("{" + ENTITY_FIELD + "." + methodName + "($$);}");
        } else {
            ctMethod.setBody("{ return " + ENTITY_FIELD + "." + methodName + "($$);}");
        }

        return ctMethod;
    }

    private CtMethod buildEnhanceMethod(CtClass ctClass, Method method, Commit commit) throws NotFoundException, CannotCompileException {
        Class<?> returnType = method.getReturnType();
        String methodName = method.getName();
        CtMethod ctMethod = new CtMethod(classPool.get(returnType.getName())
                , methodName
                , toCtClassArray(method.getParameterTypes())
                , ctClass);
        ctMethod.setModifiers(method.getModifiers());

        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length != 0) {
            ctMethod.setExceptionTypes(toCtClassArray(method.getParameterTypes()));
        }

        if (returnType == void.class) {
            ctMethod.setBody("{" + ENTITY_FIELD + "." + methodName + "($$); " +
                    "" + PERSISTER_FIELD +".commit(" + ENTITY_FIELD + ");}");
        } else {
            String returnClass = returnType.isArray() ? toArrayTypeDeclared(returnType) : returnType.getName();
            ctMethod.setBody("{" + returnClass + " ret = " + ENTITY_FIELD + "." + methodName + "($$); "
                    + PERSISTER_FIELD +".commit(" + ENTITY_FIELD + ");"
                    + "return ret;}");
        }


        return ctMethod;
    }


    /** 获取数组类型的声明定义  **/
    private String toArrayTypeDeclared(Class<?> arrayClz) {
        Class<?> type = arrayClz.getComponentType();
        return type.getName() + "[]";
    }

    /**
     * 将{@link Class}转换为{@link CtClass}
     */
    private CtClass[] toCtClassArray(Class<?>... classes) throws NotFoundException {
        if (classes == null || classes.length == 0) {
            return new CtClass[0];
        }
        CtClass[] result = new CtClass[classes.length];
        for (int i = 0; i < classes.length; i++) {
            result[i] = classPool.get(classes[i].getName());
        }
        return result;
    }
}
