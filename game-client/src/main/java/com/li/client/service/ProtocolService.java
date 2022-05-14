package com.li.client.service;

import com.li.network.anno.SocketController;
import com.li.network.anno.SocketPush;
import com.li.network.anno.SocketResponse;
import com.li.network.message.SocketProtocol;
import com.li.network.protocol.ProtocolMethodCtx;
import com.li.network.utils.ProtocolUtil;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author li-yuanwen
 * @date 2022/5/6
 */
@Service
public class ProtocolService implements ResourceLoaderAware {

    @Value("${server.protocol.package:classpath*:com/li/protocol/**/**/protocol/*.class}")
    private String protocolPackage;

    private ResourceLoader resourceLoader;

    /** 所有的协议 **/
    private Map<SocketProtocol, ProtocolMethodCtx> protocolMethodCtxHolder = new HashMap<>();

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    private void init() throws IOException, ClassNotFoundException {
        ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        MetadataReaderFactory metaReader = new CachingMetadataReaderFactory(resourceLoader);
        Resource[] resources = resolver.getResources(protocolPackage);

        final Map<SocketProtocol, ProtocolMethodCtx> sharedProtocolMethodHolder = new HashMap<>(128);
        final Map<SocketProtocol, Class<?>> returnClzHolder = new HashMap<>(64);
        for (Resource r : resources) {
            MetadataReader reader = metaReader.getMetadataReader(r);
            Class<?> aClass = Class.forName(reader.getClassMetadata().getClassName());
            if (!reader.getClassMetadata().isInterface()) {
                SocketResponse socketResponse = AnnotationUtils.findAnnotation(aClass, SocketResponse.class);
                if (socketResponse == null) {
                    continue;
                }
                SocketProtocol protocol = new SocketProtocol(socketResponse.module(), socketResponse.id());
                Class<?> oldClz = returnClzHolder.putIfAbsent(protocol, aClass);
                if (oldClz != null) {
                    throw new BeanInitializationException("出现相同协议号["
                            + protocol + "]的返回对象类型");
                }
            } else {
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
                    ProtocolMethodCtx old = sharedProtocolMethodHolder.putIfAbsent(protocolMethodCtx.getProtocol(), protocolMethodCtx);
                    if (old != null) {
                        throw new BeanInitializationException("出现相同协议号["
                                + protocolMethodCtx.getProtocol()
                                + "]");
                    }
                }
            }
        }

        for (Map.Entry<SocketProtocol, Class<?>> entry : returnClzHolder.entrySet()) {
            ProtocolMethodCtx ctx = sharedProtocolMethodHolder.get(entry.getKey());
            if (ctx.isSyncMethod()) {
                Method method = ctx.getMethod();
                if (!entry.getValue().isAssignableFrom(method.getReturnType())) {
                    throw new BeanInitializationException("协议号["
                            + entry.getKey() + "]的返回对象类型注解非法");
                }
            }
            ctx.setReturnClz(entry.getValue());
        }


        protocolMethodCtxHolder = sharedProtocolMethodHolder;

    }

    public Collection<ProtocolMethodCtx> getMethodCtxHolder() {
        return protocolMethodCtxHolder.values();
    }

    public ProtocolMethodCtx getProtocolMethodCtxBySocketProtocol(SocketProtocol protocol) {
        return protocolMethodCtxHolder.get(protocol);
    }
}
