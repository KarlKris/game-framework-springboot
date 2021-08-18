package com.li.gamecore.dao.javassist;

import com.li.gamecommon.exception.EnhanceException;
import com.li.gamecore.dao.IEntity;
import com.li.gamecore.dao.anno.Enhance;
import com.li.gamecore.dao.core.DataBasePersistor;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

import static com.li.gamecore.dao.javassist.JavassistConstants.ENHANCE_SUFFIX;
import static com.li.gamecore.dao.javassist.JavassistConstants.PERSISTOR_FIELD;

/**
 * @author li-yuanwen
 * 基于Javassist的代理工厂
 */
@Slf4j
@Component
@ConditionalOnBean(SessionFactory.class)
public class JavassistProxyFactory {

    private final ClassPool classPool = ClassPool.getDefault();
    /** 构造器缓存 **/
    private static final ConcurrentHashMap<String, Constructor<? extends IEntity>> constructorHolder = new ConcurrentHashMap<>();

    @Autowired
    private DataBasePersistor persistor;

    /** 创建实体增强对象 **/
    public <T extends IEntity> T transform(T entity) {
        Class<? extends IEntity> entityClass = entity.getClass();
        Constructor<? extends IEntity> constructor = constructorHolder.computeIfAbsent(entityClass.getName(), k -> {
            try {
                return buildEnhanceClass(entityClass).getConstructor(entityClass, DataBasePersistor.class);
            } catch (NotFoundException | CannotCompileException | NoSuchMethodException e) {
                log.error("增强类[{}]出现未知异常", entityClass.getSimpleName(), e);
                throw new EnhanceException(entity, e);
            }
        });
        try {
            return (T) constructor.newInstance(entity, persistor);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("增强类[{}]出现未知异常", entityClass.getSimpleName(), e);
            throw new EnhanceException(entity, e);
        }
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

    /**
     * 获取数组类型的声明定义
     */
    private String toArrayTypeDeclared(Class<?> arrayClz) {
        Class<?> type = arrayClz.getComponentType();
        return type.getName() + "[]";
    }

    private Class buildEnhanceClass(Class tClass)throws NotFoundException, CannotCompileException {
        CtClass superClass = classPool.get(tClass.getName());
        CtClass ctClass = classPool.makeClass(tClass.getCanonicalName() + ENHANCE_SUFFIX);
        ctClass.setSuperclass(superClass);
        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();

        // 增加@Table注解 在增强类上
        AnnotationsAttribute tableAnnotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation tableAnnotation = new Annotation("javax.persistence.Table", constPool);
        tableAnnotation.addMemberValue("name", new StringMemberValue(tClass.getSimpleName(), constPool));
        tableAnnotationsAttribute.addAnnotation(tableAnnotation);
        classFile.addAttribute(tableAnnotationsAttribute);

        // 增加持久化接口
        CtField ctField = new CtField(classPool.get(DataBasePersistor.class.getName()), PERSISTOR_FIELD, ctClass);
        ctField.setModifiers(Modifier.PRIVATE + Modifier.FINAL);
        // 增加@Transient注解 在DataBasePersistor属性上
        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation("javax.persistence.Transient", constPool);
        annotationsAttribute.addAnnotation(annotation);
        ctField.getFieldInfo().addAttribute(annotationsAttribute);
        ctClass.addField(ctField);

        // 增加构造器
        CtConstructor ctConstructor = new CtConstructor(toCtClassArray(tClass, DataBasePersistor.class), ctClass);

        StringBuilder stringBuilder = new StringBuilder("{ this.").append(PERSISTOR_FIELD).append("=$2;");
        ReflectionUtils.doWithFields(tClass, field -> {
            String fieldName = field.getName();
            stringBuilder.append("this.").append(fieldName)
                    .append("=$1.").append(fieldName).append(";");
        });
        stringBuilder.append("}");

        ctConstructor.setBody(stringBuilder.toString());
        ctConstructor.setModifiers(Modifier.PUBLIC);
        ctClass.addConstructor(ctConstructor);

        // 增强方法
        ReflectionUtils.doWithMethods(tClass, method -> {
            Enhance enhance = AnnotationUtils.findAnnotation(method, Enhance.class);
            if (enhance != null) {
                try {
                    CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
                    ctMethod.insertAfter("this." + PERSISTOR_FIELD + ".asynPersist(PersistType.UPDATE, this);");
                } catch (NotFoundException | CannotCompileException e) {
                    log.error("增强类[{}]方法[{}]出现未知异常", tClass.getSimpleName(), method.getName(), e);
                    throw new EnhanceException(tClass, e);
                }
            }
        });

        return ctClass.toClass();
    }
}
