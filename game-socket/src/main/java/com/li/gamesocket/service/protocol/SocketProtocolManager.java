package com.li.gamesocket.service.protocol;

import com.li.gamesocket.utils.CommandUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 业务协议容器
 * @author li-yuanwen
 * @date 2021/12/10
 */
@Slf4j
@Component
public class SocketProtocolManager extends InstantiationAwareBeanPostProcessorAdapter {

    /** 所有的协议 **/
    private final Map<SocketProtocol, MethodCtx> allProtocolMethodCtx = new HashMap<>(128);
    /** 协议执行上下文 **/
    private final Map<SocketProtocol, MethodInvokeCtx> invokeCtxHolder = new HashMap<>();

    @PostConstruct
    private void init() {
        // todo 扫描协议包下的所有的协议
    }

    /**
     * 获取服务器能用的模块号集
     * @return 服务器能用的模块号集
     */
    public List<Short> getProtocolModules() {
        return invokeCtxHolder.keySet().stream().map(SocketProtocol::getModule).collect(Collectors.toList());
    }

    public MethodInvokeCtx getMethodInvokeCtx(SocketProtocol protocol) {
        return invokeCtxHolder.get(protocol);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        List<MethodCtx> methodCtxHolder = CommandUtils.getMethodCtxBySocketCommand(AopUtils.getTargetClass(bean));
        if (CollectionUtils.isEmpty(methodCtxHolder)) {
            return bean;
        }

        for (MethodCtx methodCtx : methodCtxHolder) {
            if (this.invokeCtxHolder.putIfAbsent(methodCtx.getProtocol(), new MethodInvokeCtx(bean, methodCtx)) != null) {
                throw new BeanInitializationException("出现相同协议号["
                        + methodCtx.getProtocol()
                        + "]");
            }
        }

        return bean;
    }
}
