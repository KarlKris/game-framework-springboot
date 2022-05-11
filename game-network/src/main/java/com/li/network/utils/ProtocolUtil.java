package com.li.network.utils;

import com.li.network.anno.*;
import com.li.network.message.SocketProtocol;
import com.li.network.protocol.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 业务协议相关工具类
 * @author li-yuanwen
 * @date 2021/12/10
 */
public class ProtocolUtil {


    /**
     * 获取对象的module号
     * @param targetClass 对象
     * @return module号
     */
    public static short getProtocolModuleByClass(Class<?> targetClass) {
        SocketController socketController = AnnotationUtils.findAnnotation(targetClass, SocketController.class);
        if (socketController == null) {
            throw new IllegalArgumentException("类[" + targetClass.getName() +"]没有@SocketController注解");
        }

        return socketController.module();
    }

    /**
     * 解析出业务方法中的执行上下文
     * @param targetClass 目标Class
     * @return /
     */
    public static List<ProtocolMethodCtx> getMethodCtxBySocketCommand(Class<?> targetClass) {

        SocketController socketController = AnnotationUtils.findAnnotation(targetClass, SocketController.class);
        if (socketController == null) {
            return Collections.emptyList();
        }

        // 模块号
        short module = socketController.module();

        List<ProtocolMethodCtx> ctx = new LinkedList<>();

        for (Method method : targetClass.getDeclaredMethods()) {
            Method annotationMethod = findAnnotationMethod(method, SocketMethod.class);
            // 忽略常规方法
            if (annotationMethod == null) {
                continue;
            }

            SocketMethod socketMethod = annotationMethod.getAnnotation(SocketMethod.class);

            // 方法标识
            byte id = socketMethod.id();

            Class<?>[] parameterTypes = annotationMethod.getParameterTypes();
            int length = parameterTypes.length;
            if (length > 3) {
                throw new IllegalArgumentException("模块号[" + module + "]方法标识[" + id + "]的方法参数大于3个");
            }

            // 方法参数
            MethodParameter[] params = new MethodParameter[length];

            Annotation[][] annotations = annotationMethod.getParameterAnnotations();
            for (int i = 0; i < length; i++) {
                Class<?> clazz = parameterTypes[i];
                for (Annotation annotation : annotations[i]) {
                    // @Identity注解
                    if (annotation instanceof Identity) {
                        if (!ClassUtils.isAssignable(IdentityMethodParameter.IDENTITY_PARAMETER.getParameterClass(), clazz)) {
                            throw new IllegalArgumentException("模块号[" + module + "]方法标识[" + id + "]的@Identity注解使用类型必须为long");
                        }
                        params[i] = IdentityMethodParameter.IDENTITY_PARAMETER;
                        break;
                    }
                    // @Session注解
                    if (annotation instanceof Session) {
                        if (!ClassUtils.isAssignable(SessionMethodParameter.SESSION_PARAMETER.getParameterClass(), clazz)) {
                            throw new IllegalArgumentException("模块号[" + module + "]方法标识[" + id + "]的@Session注解使用类型必须为ISession");
                        }
                        params[i] = SessionMethodParameter.SESSION_PARAMETER;
                        break;
                    }
                    // @InBody注解
                    if (annotation instanceof InBody) {
                        params[i] = new InBodyMethodParameter(clazz);
                        break;
                    }
                    // @PushIds注解
                    if (annotation instanceof PushIds) {
                        params[i] = PushIdsMethodParameter.PUSH_IDS_PARAMETER;
                        break;
                    }

                    throw new IllegalArgumentException("模块号[" + module + "]方法标识[" + id + "]的方法参数没有使用相关注解");
                }
            }

            Class<?> returnClz = null;
            if (socketMethod.isSyncMethod()) {
                Class<?> type = method.getReturnType();
                if (!Void.TYPE.isAssignableFrom(type)
                        && !Number.class.isAssignableFrom(type)
                        && !Collection.class.isAssignableFrom(type)
                        && !Map.class.isAssignableFrom(type)
                        && !CompletableFuture.class.isAssignableFrom(type)) {
                    SocketResponse socketResponse = AnnotationUtils.findAnnotation(type, SocketResponse.class);
                    if (socketResponse == null) {
                        throw new IllegalArgumentException("模块号[" + module + "]方法标识[" + id + "]的方法返回对象没有使用相关注解");
                    }

                    if (socketResponse.module() != module || socketResponse.id() != id) {
                        throw new IllegalArgumentException("模块号[" + module + "]方法标识[" + id + "]的方法返回对象注解的内容不一致");
                    }
                }
                returnClz = type;
            }

            ctx.add(new ProtocolMethodCtx(new SocketProtocol(module, id), method, params, socketMethod.isSyncMethod(), returnClz));
        }

        return ctx;
    }

    /**
     * 推送命令解析方法执行上下文
     * @param targetClass 目标Class
     * @return /
     */
    public static List<ProtocolMethodCtx> getMethodCtxBySocketPush(Class<?> targetClass) {
        // 非接口忽略
        if (!targetClass.isInterface()) {
            return Collections.emptyList();
        }

        SocketPush socketPush = AnnotationUtils.findAnnotation(targetClass, SocketPush.class);
        if (socketPush == null) {
            return Collections.emptyList();
        }

        SocketController socketController = AnnotationUtils.findAnnotation(targetClass, SocketController.class);
        if (socketController == null) {
            throw new IllegalArgumentException("推送接口" + targetClass.getSimpleName() + "必须含有@SocketController");
        }

        // 模块号
        short module = socketController.module();

        List<ProtocolMethodCtx> ctx = new LinkedList<>();
        for (Method method : targetClass.getDeclaredMethods()) {
            Method annotationMethod = findAnnotationMethod(method, SocketMethod.class);
            // 忽略常规方法
            if (annotationMethod == null) {
                throw new IllegalArgumentException("接口" + targetClass.getSimpleName() + "方法必须含有@SocketPush注解");
            }


            SocketMethod socketMethod = annotationMethod.getAnnotation(SocketMethod.class);
            // 方法标识
            byte id = socketMethod.id();
            if (id >= 0) {
                throw new IllegalArgumentException("推送 模块号[" + module + "]方法标识:" + id + ">= 0");
            }

            Class<?>[] parameterTypes = annotationMethod.getParameterTypes();
            int length = parameterTypes.length;
            if (length > 2) {
                throw new IllegalArgumentException("推送 模块号[" + module + "]方法标识[" + id + "]的方法参数大于2个");
            }

            // 方法参数
            MethodParameter[] params = new MethodParameter[length];

            Annotation[][] annotations = annotationMethod.getParameterAnnotations();
            for (int i = 0; i < length; i++) {
                Class<?> clazz = parameterTypes[i];
                for (Annotation annotation : annotations[i]) {
                    // @PushIds注解
                    if (annotation instanceof PushIds) {
                        if (!ClassUtils.isAssignable(PushIdsMethodParameter.PUSH_IDS_PARAMETER.getParameterClass(), clazz)) {
                            throw new IllegalArgumentException("推送 模块号[" + module + "]方法标识[" + id + "]的@PushIds注解使用类型必须为Collection<Long>");
                        }
                        params[i] = PushIdsMethodParameter.PUSH_IDS_PARAMETER;
                        break;
                    }
                    // @InBody注解
                    if (annotation instanceof InBody) {
                        params[i] = new InBodyMethodParameter(clazz);
                        break;
                    }

                    throw new IllegalArgumentException("推送 模块号[" + module + "]方法标识[" + id + "]的方法参数没有使用相关注解");
                }
            }

            ctx.add(new ProtocolMethodCtx(new SocketProtocol(module, id), method, params, true));

        }

        return ctx;
    }


    /**
     * 查找被指定注解指定的原始方法
     * @param method 方法
     * @param targetAnnotation 注解
     * @return 原方法
     */
    public static Method findAnnotationMethod(Method method, Class<? extends Annotation> targetAnnotation) {
        Annotation annotation = method.getAnnotation(targetAnnotation);
        if (annotation != null) {
            return method;
        }
        Class<?> declaringClass = method.getDeclaringClass();
        Class<?>[] interfaces = declaringClass.getInterfaces();
        if (interfaces.length == 0) {
            return null;
        }
        for (Class<?> inter : interfaces) {
            try {
                Method interMethod = inter.getMethod(method.getName(), method.getParameterTypes());
                annotation = interMethod.getAnnotation(targetAnnotation);
                if (annotation != null) {
                    return interMethod;
                }
            } catch (NoSuchMethodException ignore) {

            }
        }
        return null;
    }

}
