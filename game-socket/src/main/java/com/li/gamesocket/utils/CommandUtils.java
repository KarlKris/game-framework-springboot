package com.li.gamesocket.utils;

import cn.hutool.core.convert.Convert;
import com.li.gamesocket.anno.*;
import com.li.gamesocket.protocol.PushResponse;
import com.li.gamesocket.protocol.Request;
import com.li.gamesocket.service.command.Command;
import com.li.gamesocket.service.protocol.MethodCtx;
import com.li.gamesocket.service.protocol.MethodParameter;
import com.li.gamesocket.service.protocol.impl.IdentityMethodParameter;
import com.li.gamesocket.service.protocol.impl.InBodyMethodParameter;
import com.li.gamesocket.service.protocol.impl.PushIdsMethodParameter;
import com.li.gamesocket.service.protocol.impl.SessionMethodParameter;
import com.li.gamesocket.service.session.ISession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author li-yuanwen
 * 命令相关工具
 */
@Slf4j
public class CommandUtils {

    /**
     * 获取对象的module号
     * @param targetClass 对象
     * @return module号
     */
    public static short getModule(Class<?> targetClass) {
        SocketController socketController = AnnotationUtils.findAnnotation(targetClass, SocketController.class);
        if (socketController == null) {
            throw new IllegalArgumentException("类[" + targetClass.getName() +"]没有@SocketModule注解");
        }

        return socketController.module();
    }

    /**
     * 解析出业务方法中的执行上下文
     * @param targetClass 目标Class
     * @return /
     */
    public static List<MethodCtx> getMethodCtxBySocketCommand(Class<?> targetClass) {
        // 接口忽略
        if (targetClass.isInterface()) {
            return Collections.emptyList();
        }

        SocketController socketController = AnnotationUtils.findAnnotation(targetClass, SocketController.class);
        if (socketController == null) {
            return Collections.emptyList();
        }

        // 模块号
        short module = socketController.module();

        List<MethodCtx> ctx = new LinkedList<>();

        for (Method method : targetClass.getDeclaredMethods()) {
            Method annotationMethod = findAnnotationMethod(method, SocketMethod.class);
            // 忽略常规方法
            if (annotationMethod == null) {
                continue;
            }

            SocketMethod socketMethod = annotationMethod.getAnnotation(SocketMethod.class);

            // 命令号
            byte command = socketMethod.command();

            Class<?>[] parameterTypes = annotationMethod.getParameterTypes();
            int length = parameterTypes.length;
            if (length > 3) {
                throw new IllegalArgumentException("模块号[" + module + "]命令号[" + command + "]的方法参数大于3个");
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
                            throw new IllegalArgumentException("模块号[" + module + "]命令号[" + command + "]的@Identity注解使用类型必须为long");
                        }
                        params[i] = IdentityMethodParameter.IDENTITY_PARAMETER;
                        break;
                    }
                    // @Session注解
                    if (annotation instanceof Session) {
                        if (!ClassUtils.isAssignable(SessionMethodParameter.SESSION_PARAMETER.getParameterClass(), clazz)) {
                            throw new IllegalArgumentException("模块号[" + module + "]命令号[" + command + "]的@Session注解使用类型必须为ISession");
                        }
                        params[i] = SessionMethodParameter.SESSION_PARAMETER;
                        break;
                    }
                    // @InBody注解
                    if (annotation instanceof InBody) {
                        params[i] = new InBodyMethodParameter(clazz);
                        break;
                    }

                    throw new IllegalArgumentException("模块号[" + module + "]命令号[" + command + "]的方法参数没有使用相关注解");
                }
            }

            ctx.add(new MethodCtx(new Command(module, command), method, params));
        }

        return ctx;
    }

    /**
     * 推送命令解析方法执行上下文
     * @param targetClass 目标Class
     * @return /
     */
    public static List<MethodCtx> getMethodCtxBySocketPush(Class<?> targetClass) {
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
            throw new IllegalArgumentException("推送接口" + targetClass.getSimpleName() + "必须含有@SocketModule注解");
        }

        // 模块号
        short module = socketController.module();

        List<MethodCtx> ctx = new LinkedList<>();
        for (Method method : targetClass.getDeclaredMethods()) {
            Method annotationMethod = findAnnotationMethod(method, SocketMethod.class);
            // 忽略常规方法
            if (annotationMethod == null) {
                throw new IllegalArgumentException("接口" + targetClass.getSimpleName() + "方法必须含有@SocketPush注解");
            }


            SocketMethod socketMethod = annotationMethod.getAnnotation(SocketMethod.class);
            // 命令号
            byte command = socketMethod.command();

            Class<?>[] parameterTypes = annotationMethod.getParameterTypes();
            int length = parameterTypes.length;
            if (length > 2) {
                throw new IllegalArgumentException("推送 模块号[" + module + "]命令号[" + command + "]的方法参数大于2个");
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
                            throw new IllegalArgumentException("推送 模块号[" + module + "]命令号[" + command + "]的@PushIds注解使用类型必须为Collection<Long>");
                        }
                        params[i] = PushIdsMethodParameter.PUSH_IDS_PARAMETER;
                        break;
                    }
                    // @InBody注解
                    if (annotation instanceof InBody) {
                        params[i] = new InBodyMethodParameter(clazz);
                        break;
                    }

                    throw new IllegalArgumentException("推送 模块号[" + module + "]命令号[" + command + "]的方法参数没有使用相关注解");
                }
            }

            ctx.add(new MethodCtx(new Command(module, command), method, params));

        }

        return ctx;
    }


    /**
     * 解析出方法参数
     *
     * @param session  session
     * @param identity 身份标识
     * @param params   方法参数信息
     * @param request  Request
     * @return 方法参数数组
     */
    public static Object[] decodeRequest(ISession session, long identity, MethodParameter[] params, Request request) {
        Map<String, Object> map = request.getParams();

        int length = params.length;
        Object[] objs = new Object[length];
        for (int i = 0; i < length; i++) {
            MethodParameter parameter = params[i];
            if (parameter instanceof SessionMethodParameter) {
                objs[i] = session;
                continue;
            }

            if (parameter instanceof IdentityMethodParameter) {
                objs[i] = identity > 0 ? identity : map.get(IdentityMethodParameter.TYPE);
                continue;
            }

            if (parameter instanceof InBodyMethodParameter) {
                InBodyMethodParameter param = (InBodyMethodParameter) parameter;
                String parameterName = param.getParameterName();
                Object o = map.get(parameterName);
                if (o != null) {
                    objs[i] = Convert.convert(param.getParameterType(), o);
                    continue;
                }

                if (!param.isRequired()) {
                    objs[i] = null;
                    continue;
                }
            }

            throw new UnsupportedOperationException("常规业务参数类型不支持[" + parameter + "]解析");
        }

        return objs;
    }


    /**
     * 将PushResponse转成方法调用参数
     * @param params 方法参数内容
     * @param pushResponse pushResponse
     * @return /
     */
    public static Object[] decodePushResponse(MethodParameter[] params, PushResponse pushResponse) {
        Map<String, Object> content = pushResponse.getContent();

        int length = params.length;
        Object[] objs = new Object[length];

        for (int i = 0; i < length; i++) {
            MethodParameter parameter = params[i];
            if (parameter instanceof InBodyMethodParameter) {
                InBodyMethodParameter param = (InBodyMethodParameter) parameter;
                String parameterName = param.getParameterName();
                Object o = content.get(parameterName);
                if (o != null) {
                    objs[i] = Convert.convert(param.getParameterType(), o);
                    continue;
                }

                if (!param.isRequired()) {
                    objs[i] = null;
                    continue;
                }
            }

            if (parameter instanceof PushIdsMethodParameter) {
                objs[i] = pushResponse.getTargets();
                continue;
            }

            throw new UnsupportedOperationException("常规业务参数类型不支持[" + parameter + "]解析");
        }

        return objs;
    }


    /**
     * 构建 RPC Request
     *
     * @param params 方法参数信息
     * @param args   实际参数
     * @return Request
     */
    public static Request encodeRpcRequest(MethodParameter[] params, Object[] args) {
        int length = params.length;
        if (length != args.length) {
            throw new IllegalArgumentException("请求参数数量与定义数量不一致");
        }

        Map<String, Object> map = new HashMap<>(params.length);

        for (int i = 0; i < length; i++) {
            MethodParameter parameter = params[i];
            // 身份标识参数
            if (parameter instanceof IdentityMethodParameter) {
                map.put(IdentityMethodParameter.TYPE, args[i]);
                continue;
            }

            // 普通参数
            if (parameter instanceof InBodyMethodParameter) {
                InBodyMethodParameter param = (InBodyMethodParameter) parameter;
                map.put(param.getParameterName(), args[i]);
                continue;
            }

            // session
            if (parameter instanceof SessionMethodParameter) {
                continue;
            }

            throw new IllegalArgumentException("RPC 调用方法参数注解非法[" + parameter.type() + "]");
        }

        return new Request(map);
    }

    /**
     * 构建推送消息中消息体
     *
     * @param params 方法参数信息
     * @param args   实际参数
     * @return PushRequest
     */
    public static PushResponse encodePushResponse(MethodParameter[] params, Object[] args) {
        int length = params.length;
        if (length != args.length) {
            throw new IllegalArgumentException("请求参数数量与定义数量不一致");
        }

        Map<String, Object> map = new HashMap<>(params.length);
        Collection<Long> identities = null;

        for (int i = 0; i < length; i++) {
            MethodParameter parameter = params[i];
            if (parameter instanceof PushIdsMethodParameter) {
                identities = (Collection<Long>) args[i];
                continue;
            }

            if (parameter instanceof InBodyMethodParameter) {
                InBodyMethodParameter param = (InBodyMethodParameter) parameter;
                map.put(param.getParameterName(), args[i]);
                continue;
            }

            throw new IllegalArgumentException("RPC请求参数不支持 除@InBody以外的其他 注解");
        }

        return new PushResponse(identities, map);
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
