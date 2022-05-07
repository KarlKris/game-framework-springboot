package com.li.client.service;

import com.li.network.anno.SocketController;
import com.li.network.anno.SocketPush;
import com.li.network.protocol.ProtocolMethodCtx;
import com.li.network.utils.ProtocolUtil;
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
import java.util.LinkedList;
import java.util.List;

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
    private final List<ProtocolMethodCtx> protocolMethodCtxHolder = new LinkedList<>();

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

            SocketPush annotation = AnnotationUtils.findAnnotation(aClass, SocketPush.class);
            if (annotation != null) {
                continue;
            }

            protocolMethodCtxHolder.addAll(ProtocolUtil.getMethodCtxBySocketCommand(aClass));

        }
    }

    public List<ProtocolMethodCtx> getMethodCtxHolder() {
        return protocolMethodCtxHolder;
    }
}
