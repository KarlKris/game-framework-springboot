package com.li.gamesocket.utils;

import cn.hutool.core.convert.Convert;
import com.li.gamesocket.anno.Identity;
import com.li.gamesocket.anno.InBody;
import com.li.gamesocket.anno.SocketCommand;
import com.li.gamesocket.anno.SocketModule;
import com.li.gamesocket.protocol.Request;
import com.li.gamesocket.service.Command;
import com.li.gamesocket.service.MethodCtx;
import com.li.gamesocket.service.MethodParameter;
import com.li.gamesocket.service.impl.IdentityMethodParameter;
import com.li.gamesocket.service.impl.InBodyMethodParameter;
import com.li.gamesocket.service.impl.SessionMethodParameter;
import com.li.gamesocket.session.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author li-yuanwen
 * 命令相关工具
 */
@Slf4j
public class CommandUtils {

    /**
     * 解析出对象中的命令上下文
     * @param targetClass 目标Class
     * @param bean 是否关心是否实例化
     * @return /
     */
    public static List<MethodCtx> analysisCommands(Class<?> targetClass, boolean bean) {
        // bean实际类型不能为接口
        if (targetClass.isInterface() && bean) {
            return Collections.emptyList();
        }

        // 忽略常规bean
        SocketModule socketModule = AnnotationUtils.findAnnotation(targetClass, SocketModule.class);
        if (socketModule == null) {
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

            Parameter[] parameters = method.getParameters();
            int length = parameters.length;
            MethodParameter[] params = new MethodParameter[length];
            for (int i = 0; i < length; i++) {
                Parameter parameter = parameters[i];

                // session
                if (parameter.getParameterizedType()
                        == SessionMethodParameter.SESSION_PARAMETER.getParameterType()) {
                    params[i] = SessionMethodParameter.SESSION_PARAMETER;
                    continue;
                }

                Identity identity = parameter.getAnnotation(Identity.class);
                if (identity != null) {
                    if (parameter.getType() != IdentityMethodParameter.IDENTITY_PARAMETER.getParameterType()) {
                        throw new BeanInitializationException("模块号[" + module + "]命令号[" + command + "]的@Identity注解使用类型必须为long");
                    }
                    params[i] = IdentityMethodParameter.IDENTITY_PARAMETER;
                    continue;
                }

                InBody inBody = parameter.getAnnotation(InBody.class);
                if (inBody != null) {
                    params[i] = new InBodyMethodParameter(inBody.name(), parameter.getType(), inBody.required());
                    continue;
                }

                if (log.isWarnEnabled()) {
                    log.warn("模块号[{}]命令号[{}]方法参数出现既没有@Identity注解也没有@InBody注解,将使用方法参数名为name属性,required为true"
                            , module, command);
                }

                params[i] = new InBodyMethodParameter(parameter.getName(), parameter.getType(), true);
            }

            ctx.add(new MethodCtx(new Command(module, command), method, params));
        }

        return ctx;
    }

    /**
     * 解析出方法参数
     * @param session session
     * @param identity 身份标识
     * @param params 方法参数信息
     * @param request Request
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

            if (parameter.identity()) {
                objs[i] = identity;
                continue;
            }

            String parameterName = parameter.getParameterName();
            Object o = map.get(parameterName);
            if (o != null) {
                objs[i] = Convert.convert(parameter.getParameterType(), o);
                continue;
            }

            if (!parameter.isRequired()) {
                objs[i] = null;
                continue;
            }

            throw new IllegalArgumentException("未提供参数[" + parameterName + "]");
        }

        return objs;
    }


    /**
     * 构建Request
     * @param params 方法参数信息
     * @param args 实际参数
     * @return Request
     */
    public static Request encodeRequest(MethodParameter[] params, Object[] args) {
        int length = params.length;
        if (length != args.length) {
            throw new IllegalArgumentException("请求参数数量与定义数量不一致");
        }

        Map<String, Object> map = new HashMap<>(params.length);

        for (int i = 0; i < length; i++) {
            MethodParameter parameter = params[i];
            if (parameter instanceof IdentityMethodParameter) {
                throw new IllegalArgumentException("请求参数类型不支持 @Identity 注解");
            }
            if (parameter instanceof SessionMethodParameter) {
                throw new IllegalArgumentException("请求参数类型不支持 Session");
            }

            map.put(parameter.getParameterName(), args[i]);
        }

        return new Request(map);
    }

}
