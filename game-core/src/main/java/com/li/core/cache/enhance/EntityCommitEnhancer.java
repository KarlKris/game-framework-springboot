package com.li.core.cache.enhance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.li.common.exception.EnhanceException;
import com.li.common.utils.ObjectUtils;
import com.li.core.dao.IEntity;
import com.li.core.dao.anno.Commit;
import com.li.core.dao.service.IDataPersistence;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

import static com.li.core.cache.enhance.EnhanceConstants.*;

/**
 * 实体自动回写@Commit注解增强器
 * @author li-yuanwen
 * @date 2022/3/14
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "spring.datasource.url")
public class EntityCommitEnhancer implements Enhancer {

    private final ClassPool classPool = ClassPool.getDefault();
    /** 构造器缓存 **/
    private final ConcurrentHashMap<String, Constructor<Object>> constructorHolder = new ConcurrentHashMap<>();

    @Resource
    private IDataPersistence persistence;

    @Override
    public <T> T enhance(T obj) {
        Class<?> entityClass = obj.getClass();
        Constructor<Object> constructor = constructorHolder.computeIfAbsent(entityClass.getName(), k -> {
            try {
                return (Constructor<Object>) buildEnhanceClass(entityClass).getConstructor(entityClass, IDataPersistence.class);
            } catch (NotFoundException | CannotCompileException | NoSuchMethodException | ClassNotFoundException e) {
                log.error("增强类[{}]出现未知异常", entityClass.getSimpleName(), e);
                throw new EnhanceException(obj, e);
            }
        });
        try {
            return (T) constructor.newInstance(obj, persistence);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("增强类[{}]出现未知异常", entityClass.getSimpleName(), e);
            throw new EnhanceException(obj, e);
        }
    }



    private <T> Class<T> buildEnhanceClass(Class<T> tClass) throws NotFoundException, CannotCompileException, ClassNotFoundException {
        // 构建增强类
        CtClass ctClass = buildCtClass(tClass);
        // 构建属性域
        buildFields(tClass, ctClass);
        // 增加构造器
        buildConstructor(tClass, ctClass);
        // 实现EnhanceEntity接口
        buildEnhanceEntityMethod(tClass, ctClass);
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
                throw new IllegalArgumentException("增强实体[" + tClass.getName() + "]方法[" + method.getName() + "]出现未知异常", e);
            }

        }, method -> {
            if (ObjectUtils.OBJECT_METHODS.contains(method)) {
                return false;
            }
            if (Modifier.isFinal(method.getModifiers()) || Modifier.isStatic(method.getModifiers())
                    || Modifier.isPrivate(method.getModifiers())) {
                return false;
            }
            return !method.isSynthetic();
        });

        return (Class<T>) ctClass.toClass();
    }

    /**
     * 创建增强类对象
     * @param entityClass 待增强对象
     * @return 增强类对象
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private CtClass buildCtClass(Class<?> entityClass) throws NotFoundException, CannotCompileException {
        String className = entityClass.getName();
        CtClass ctClass = classPool.makeClass(entityClass.getCanonicalName() + ENHANCE_SUFFIX);
        CtClass superClass = classPool.get(className);
        ctClass.setSuperclass(superClass);
        ctClass.addInterface(classPool.get(EnhanceEntity.class.getName()));
        return ctClass;
    }

    /**
     * 构建增强类的属性域
     * @param entityClass
     * @param enhanceClass
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private void buildFields(Class<?> entityClass, CtClass enhanceClass) throws NotFoundException, CannotCompileException {
        // 增加持久化Field
        CtField persistField = new CtField(classPool.get(IDataPersistence.class.getName()), PERSISTENCE_FIELD, enhanceClass);
        persistField.setModifiers(Modifier.PRIVATE + Modifier.FINAL);
        enhanceClass.addField(persistField);
        // 增加实际实体
        CtField entityField = new CtField(classPool.get(entityClass.getName()), ENTITY_FIELD, enhanceClass);
        entityField.setModifiers(Modifier.PRIVATE + Modifier.FINAL);
        enhanceClass.addField(entityField);
    }

    /**
     * 构建增强类的构造方法
     * @param entityClass
     * @param enhanceClass
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private void buildConstructor(Class<?> entityClass, CtClass enhanceClass) throws NotFoundException, CannotCompileException {
        // 增加构造器
        CtConstructor ctConstructor = new CtConstructor(toCtClassArray(entityClass, IDataPersistence.class), enhanceClass);

        String constructorBody = "{ this." + ENTITY_FIELD + "=$1;" +
                "this." + PERSISTENCE_FIELD + "=$2;}";
        ctConstructor.setBody(constructorBody);
        ctConstructor.setModifiers(Modifier.PUBLIC);
        enhanceClass.addConstructor(ctConstructor);
    }

    /**
     * 实现EnhanceEntity接口
     * @param entityClass
     * @param enhanceClass
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private void buildEnhanceEntityMethod(Class<?> entityClass, CtClass enhanceClass) throws NotFoundException, CannotCompileException {
        // 增强接口EnhanceEntity:getEntity 返回类型
        CtClass returnClz = classPool.get(IEntity.class.getName());
        CtClass[] parameters = new CtClass[0];
        ConstPool cp = enhanceClass.getClassFile2().getConstPool();
        String desc = Descriptor.ofMethod(returnClz, parameters);
        MethodInfo methodInfo = new MethodInfo(cp, METHOD_GET_ENTITY, desc);

        // 添加JsonIgnore的注释到方法
        AnnotationsAttribute annoAttr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
        javassist.bytecode.annotation.Annotation annot = new javassist.bytecode.annotation.Annotation(JsonIgnore.class.getName(), cp);
        annoAttr.addAnnotation(annot);
        methodInfo.addAttribute(annoAttr);

        // 创建方法对象
        CtMethod method = CtMethod.make(methodInfo, enhanceClass);
        String methodBody = "{ return this." + ENTITY_FIELD + ";}";
        method.setBody(methodBody);
        enhanceClass.addMethod(method);
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
                    "" + PERSISTENCE_FIELD +".commit(" + ENTITY_FIELD + ");}");
        } else {
            String returnClass = returnType.isArray() ? toArrayTypeDeclared(returnType) : returnType.getName();
            ctMethod.setBody("{" + returnClass + " ret = " + ENTITY_FIELD + "." + methodName + "($$); "
                    + PERSISTENCE_FIELD +".commit(" + ENTITY_FIELD + ");"
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
