package com.li.gamecommon.resource.core;

import cn.hutool.core.util.ArrayUtil;
import com.li.gamecommon.resource.anno.ResourceObj;
import com.li.gamecommon.resource.anno.ResourceScan;
import com.li.gamecommon.resource.storage.StorageManagerFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 资源注册
 * @author li-yuanwen
 * @date 2022/3/16
 */
@Slf4j
@Component
public class ResourceBeanPostFactory implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private final Set<Integer> registriesPostProcessed = new HashSet<>();

    /** 默认资源匹配符 */
    protected static final String DEFAULT_RESOURCE_PATTERN = "/**/*.class";

    /** 资源搜索分析器，由它来负责检索EAO接口 */
    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    /** 类的元数据读取器，由它来负责读取类上的注释信息 */
    private final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        processResourceBeanDefinition(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        processResourceBeanDefinition((BeanDefinitionRegistry) beanFactory);
    }


    private void processResourceBeanDefinition(BeanDefinitionRegistry registry) {
        int registryId = System.identityHashCode(registry);
        if (this.registriesPostProcessed.add(registryId)) {
            // BeanDefinitionRegistryPostProcessor hook apparently not supported...
            // Simply call processConfigurationClasses lazily at this point then.
            processResourceBeanDefinition0(registry);
        }
    }

    private void processResourceBeanDefinition0(BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = getResourceScanAnnotationAttributes(registry);
        if (attributes == null) {
            return;
        }
        // 资源类路径
        String[] basePackages = attributes.getStringArray("value");
        if (ArrayUtil.isEmpty(basePackages)) {
            return;
        }

        ManagedList<ResourceDefinition> resourceDefinitions = new ManagedList<>();
        // 文件路径,支持${}
        String rootPath = attributes.getString("path");
        for (String basePackage : basePackages) {
            for (String resourceClass : getResources(basePackage) ){
                Class<?> clz = null;
                try {
                    clz = Class.forName(resourceClass);
                } catch (ClassNotFoundException e) {
                    log.error("资源对象{}未找到对应的Class文件", resourceClass, e);
                }

                if (clz == null) {
                    continue;
                }

                ResourceDefinition resourceDefinition = parseResourceDefinition(clz, rootPath);
                if (resourceDefinition != null) {
                    resourceDefinitions.add(resourceDefinition);
                }
            }
        }

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(StorageManagerFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("definitions", resourceDefinitions);

        // 注册
        registerBeanDefinition(registry, beanDefinitionBuilder.getBeanDefinition());
    }


    private AnnotationAttributes getResourceScanAnnotationAttributes(BeanDefinitionRegistry registry) {
        String[] beanDefinitionNames = registry.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanDefinitionName);
            String className = beanDef.getBeanClassName();
            if (className == null || beanDef.getFactoryMethodName() != null) {
                continue;
            }
            AnnotationMetadata metadata;
            if (beanDef instanceof AnnotatedBeanDefinition &&
                    className.equals(((AnnotatedBeanDefinition) beanDef).getMetadata().getClassName())) {
                // Can reuse the pre-parsed metadata from the given BeanDefinition...
                metadata = ((AnnotatedBeanDefinition) beanDef).getMetadata();
            }
            else if (beanDef instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) beanDef).hasBeanClass()) {
                // Check already loaded Class if present...
                // since we possibly can't even load the class file for this Class.
                Class<?> beanClass = ((AbstractBeanDefinition) beanDef).getBeanClass();
                if (BeanFactoryPostProcessor.class.isAssignableFrom(beanClass) ||
                        BeanPostProcessor.class.isAssignableFrom(beanClass) ||
                        AopInfrastructureBean.class.isAssignableFrom(beanClass) ||
                        EventListenerFactory.class.isAssignableFrom(beanClass)) {
                    continue;
                }
                metadata = AnnotationMetadata.introspect(beanClass);
            }
            else {
                try {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
                    metadata = metadataReader.getAnnotationMetadata();
                }
                catch (IOException ex) {
                    if (log.isDebugEnabled()) {
                        log.debug("Could not find class file for introspecting configuration annotations: " +
                                className, ex);
                    }
                    continue;
                }
            }

            return AnnotationAttributes.fromMap(
                    metadata.getAnnotationAttributes(ResourceScan.class.getName(), false));
        }
        return null;
    }

    private Set<String> getResources(String basePackage) {
        try {
            Set<String> resourceClzNames = new HashSet<>(64);
            String resourceAnnotation = ResourceObj.class.getName();

            // 搜索资源
            String basePackageName = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(basePackageName);
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }
                // 判断是否是资源
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (!metadataReader.getAnnotationMetadata().hasAnnotation(resourceAnnotation)) {
                    continue;
                }
                String className = metadataReader.getClassMetadata().getClassName();
                if (className.equals(resourceAnnotation)) {
                    continue;
                }
                resourceClzNames.add(className);
            }

            return resourceClzNames;
        } catch (Exception e) {
            throw new RuntimeException("读取资源类时发生未知异常", e);
        }
    }

    @Nullable
    private ResourceDefinition parseResourceDefinition(Class<?> clz, String rootPath) {
        ResourceObj obj = AnnotationUtils.findAnnotation(clz, ResourceObj.class);
        if (obj == null) {
            return null;
        }
        return new ResourceDefinition(clz, environment.resolvePlaceholders(rootPath));
    }

    private void registerBeanDefinition(BeanDefinitionRegistry registry, BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        if (beanClassName == null) {
            throw new RuntimeException("注册BeanDefinition时beanClassName = null");
        }
        String beanName = StringUtils.uncapitalize(beanClassName.substring(beanClassName.lastIndexOf(".") + 1));
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

}
