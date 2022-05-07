package com.li.network.protocol;

import com.li.network.anno.SocketController;
import com.li.network.anno.SocketPush;
import com.li.network.message.SocketProtocol;
import com.li.network.utils.ProtocolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 业务协议容器
 * @author li-yuanwen
 * @date 2021/12/10
 */
@Slf4j
@Component
public class SocketProtocolManager implements SmartInstantiationAwareBeanPostProcessor, ResourceLoaderAware {

    @Value("${server.protocol.package:classpath*:com/li/protocol/**/**/protocol/*.class}")
    private String protocolPackage;

    private ResourceLoader resourceLoader;

    /** 所有的协议 **/
    private final Map<SocketProtocol, ProtocolMethodCtx> allProtocolMethodCtx = new HashMap<>(128);
    /** 协议执行上下文 **/
    private final Map<SocketProtocol, ProtocolMethodInvokeCtx> invokeCtxHolder = new HashMap<>();

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    private void init() throws IOException, ClassNotFoundException {
        ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        MetadataReaderFactory metaReader = new CachingMetadataReaderFactory(resourceLoader);
        Resource[] resources = resolver.getResources(protocolPackage);

        for (Resource r : resources) {
            MetadataReader reader = metaReader.getMetadataReader(r);
            if (!reader.getClassMetadata().isInterface()) {
                continue;
            }

            Class<?> aClass = Class.forName(reader.getClassMetadata().getClassName());
            SocketController socketController = AnnotationUtils.findAnnotation(aClass, SocketController.class);
            if (socketController == null) {
                continue;
            }

            List<ProtocolMethodCtx> ctx = null;

            SocketPush annotation = AnnotationUtils.findAnnotation(aClass, SocketPush.class);
            if (annotation == null) {
                ctx = ProtocolUtil.getMethodCtxBySocketCommand(aClass);
            } else {
                ctx = ProtocolUtil.getMethodCtxBySocketPush(aClass);
            }

            for (ProtocolMethodCtx protocolMethodCtx : ctx) {
                ProtocolMethodCtx old = allProtocolMethodCtx.putIfAbsent(protocolMethodCtx.getProtocol(), protocolMethodCtx);
                if (old != null) {
                    throw new BeanInitializationException("出现相同协议号["
                            + protocolMethodCtx.getProtocol()
                            + "]");
                }
            }

        }
    }

    /**
     * 获取服务器负责的模块号集
     * @return 服务器能用的模块号集
     */
    public Set<Short> getProtocolModules() {
        // 过滤掉推送模块
        return invokeCtxHolder.keySet()
                .stream()
                .filter(socketProtocol -> !socketProtocol.isPushProtocol())
                .map(SocketProtocol::getModule)
                .collect(Collectors.toSet());
    }

    /**
     * 获取协议的执行上下文
     * @param protocol 协议
     * @return 执行上下文 or null
     */
    public ProtocolMethodInvokeCtx getMethodInvokeCtx(SocketProtocol protocol) {
        return invokeCtxHolder.get(protocol);
    }


    /**
     * 获取协议上下文
     * @param protocol 协议
     * @return 协议上下文,理论上不会返回null
     */
    public ProtocolMethodCtx getMethodCtxBySocketProtocol(SocketProtocol protocol) {
        return allProtocolMethodCtx.get(protocol);
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        List<ProtocolMethodCtx> protocolMethodCtxHolder = ProtocolUtil.getMethodCtxBySocketCommand(AopUtils.getTargetClass(bean));
        if (CollectionUtils.isEmpty(protocolMethodCtxHolder)) {
            return bean;
        }

        for (ProtocolMethodCtx protocolMethodCtx : protocolMethodCtxHolder) {
            if (this.invokeCtxHolder.putIfAbsent(protocolMethodCtx.getProtocol(), new ProtocolMethodInvokeCtx(bean, protocolMethodCtx)) != null) {
                throw new BeanInitializationException("出现相同协议号["
                        + protocolMethodCtx.getProtocol()
                        + "]");
            }
        }

        return bean;
    }
}
