package com.li.gamesocket.utils;

import cn.hutool.core.convert.Convert;
import com.li.gamesocket.anno.*;
import com.li.gamesocket.protocol.PushResponse;
import com.li.gamesocket.protocol.Request;
import com.li.gamesocket.service.command.Command;
import com.li.gamesocket.service.command.MethodCtx;
import com.li.gamesocket.service.command.MethodParameter;
import com.li.gamesocket.service.command.impl.IdentityMethodParameter;
import com.li.gamesocket.service.command.impl.InBodyMethodParameter;
import com.li.gamesocket.service.command.impl.PushIdsMethodParameter;
import com.li.gamesocket.service.command.impl.SessionMethodParameter;
import com.li.gamesocket.service.session.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
        SocketModule socketModule = AnnotationUtils.findAnnotation(targetClass, SocketModule.class);
        if (socketModule == null) {
            throw new IllegalArgumentException("类[" + targetClass.getName() +"]没有@SocketModule注解");
        }

        return socketModule.module();
    }

    /**
     * 解析出对象中的命令上下文
     *
     * @param targetClass 目标Class
     * @param bean        是否关心是否实例化
     * @return /
     */
    public static List<MethodCtx> analysisCommands(Class<?> targetClass, boolean bean) {
        // bean实际类型不能为接口
        if (targetClass.isInterface() && bean) {
            return Collections.emptyList();
        }

        SocketModule socketModule = AnnotationUtils.findAnnotation(targetClass, SocketModule.class);
        boolean push = AnnotationUtils.findAnnotation(targetClass, SocketPush.class) != null;
        // 既不是业务，也不是推送
        if (socketModule == null && !push) {
            return Collections.emptyList();
        }


        // 模块号
        short module = socketModule.module();

        Method[] declaredMethods = targetClass.getDeclaredMethods();
        List<MethodCtx> ctx = new ArrayList<>(declaredMethods.length);

        // 检查方法
        for (Method method : declaredMethods) {
            SocketCommand socketCommand = AnnotationUtils.findAnnotation(method, SocketCommand.class);
            // 忽略常规方法
            if (socketCommand == null) {
                continue;
            }

            // 命令号
            byte command = socketCommand.command();

            Type[] genericParameterTypes = method.getGenericParameterTypes();
            int length = genericParameterTypes.length;
            MethodParameter[] params = new MethodParameter[length];

            Annotation[][] annotations = method.getParameterAnnotations();
            for (int i = 0; i < length; i++) {
                Type type = genericParameterTypes[i];
                // session不得用于推送
                if (!push && TypeUtils.isAssignable(SessionMethodParameter.SESSION_PARAMETER.getParameterType(), type)) {
                    params[i] = SessionMethodParameter.SESSION_PARAMETER;
                    continue;
                }

                boolean hasAnnotation = false;
                for (Annotation annotation : annotations[i]) {
                    // identity不得用于推送
                    if (!push && annotation instanceof Identity) {
                        if (!TypeUtils.isAssignable(IdentityMethodParameter.IDENTITY_PARAMETER.getParameterType(), type)) {
                            throw new IllegalArgumentException("模块号[" + module + "]命令号[" + command + "]的@Identity注解使用类型必须为long");
                        }
                        params[i] = IdentityMethodParameter.IDENTITY_PARAMETER;
                        hasAnnotation = true;
                        break;
                    }
                    // 参数内容
                    if (annotation instanceof InBody) {
                        InBody inBody = (InBody) annotation;
                        params[i] = new InBodyMethodParameter(inBody.name(), type, inBody.required());
                        hasAnnotation = true;
                        break;
                    }
                    // 推送目标
                    if (annotation instanceof PushIds) {
                        if (push && TypeUtils.isAssignable(PushIdsMethodParameter.PUSH_IDS_METHOD_PARAMETER.getParameterType(), type)) {
                            params[i] = PushIdsMethodParameter.PUSH_IDS_METHOD_PARAMETER;
                            hasAnnotation = true;
                            break;
                        }
                        throw new IllegalArgumentException("@PushIds注解使用不符合规范");
                    }
                }

                if (!hasAnnotation) {
                    log.warn("模块号[{}]命令号[{}]方法参数出现既没有任何注解,将使用方法参数名为name属性,required为true"
                            , module, command);
                    params[i] = new InBodyMethodParameter(type.getTypeName(), type, true);
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
    public static Object[] decodeRequest(Session session, long identity, MethodParameter[] params, Request request) {
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
                objs[i] = identity;
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
            if (parameter instanceof InBodyMethodParameter) {
                InBodyMethodParameter param = (InBodyMethodParameter) parameter;
                map.put(param.getParameterName(), args[i]);
                continue;
            }

            throw new IllegalArgumentException("RPC请求参数不支持 除@InBody以外的其他 注解");
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

}
