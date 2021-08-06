package com.li.gamesocket.service.command;

import com.li.gamesocket.utils.CommandUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author li-yuanwen
 * @date 2021/7/30 21:06
 * 命令管理器
 **/
@Component
@Slf4j
public class CommandManager extends InstantiationAwareBeanPostProcessorAdapter {

    /** 命令执行器 **/
    private Map<Command, MethodInvokeCtx> commandInvokeCtxHolder = new HashMap<>();

    public MethodInvokeCtx getMethodInvokeCtx(Command command) {
        return commandInvokeCtxHolder.get(command);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        List<MethodCtx> methodCtxHolder = CommandUtils.analysisCommands(AopUtils.getTargetClass(bean), true);
        if (CollectionUtils.isEmpty(methodCtxHolder)) {
            return bean;
        }

        for (MethodCtx methodCtx : methodCtxHolder) {
            if (this.commandInvokeCtxHolder.putIfAbsent(methodCtx.getCommand(), new MethodInvokeCtx(bean, methodCtx)) != null) {
                throw new BeanInitializationException("出现相同命令--模块号["
                        + methodCtx.getCommand().getModule()
                        + "]命令号["
                        + methodCtx.getCommand()
                        + "]");
            }
        }

        return bean;
    }
}