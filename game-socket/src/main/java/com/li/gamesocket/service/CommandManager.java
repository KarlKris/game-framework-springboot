package com.li.gamesocket.service;

import com.li.gamesocket.anno.Identity;
import com.li.gamesocket.anno.InBody;
import com.li.gamesocket.anno.SocketCommand;
import com.li.gamesocket.anno.SocketModule;
import com.li.gamesocket.service.impl.IdentityMethodParameter;
import com.li.gamesocket.service.impl.InBodyMethodParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author li-yuanwen
 * @date 2021/7/30 21:06
 * 命令管理器
 **/
@Component
@Slf4j
public class CommandManager extends InstantiationAwareBeanPostProcessorAdapter {

    /**
     * 命令执行器
     **/
    private Map<Command, MethodInvokeCtx> commandInvokeCtxHolder = new HashMap<>();

    public MethodInvokeCtx getMethodInvokeCtx(Command command) {
        return commandInvokeCtxHolder.get(command);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 实际类型不能为接口
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (targetClass.isInterface()) {
            return bean;
        }

        // 忽略常规bean
        SocketModule socketModule = AnnotationUtils.findAnnotation(targetClass, SocketModule.class);
        if (socketModule == null) {
            return bean;
        }

        // 模块号
        short module = socketModule.module();

        // 检查方法
        for (Method method : targetClass.getDeclaredMethods()) {
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

            if (this.commandInvokeCtxHolder.putIfAbsent(new Command(module, command)
                    , new MethodInvokeCtx(bean, method, params)) != null) {
                throw new BeanInitializationException("出现相同的模块号[" + module + "]命令号[" + command + "]");
            }

        }

        return bean;
    }
}
