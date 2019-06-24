package com.yoloho.spring.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.yoloho.spring.util.CustomPropertySourcesPlaceholderConfigurer;
import com.yoloho.spring.util.InitDefaults;

/**
 * 默认配置的spring-boot支持
 * 
 * @author jason
 *
 */
public class InitDefaultsConfiguration implements DeferredImportSelector {
    public static class DefaultsConfiguration implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                BeanDefinitionRegistry registry) {
            {
                // placeholder 合并进来
                String[] locations = (String[])importingClassMetadata.getAnnotationAttributes(com.yoloho.spring.annotation.InitDefaults.class.getName()).get("propertyLocations");
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CustomPropertySourcesPlaceholderConfigurer.class);
                builder.setLazyInit(false);
                if (locations.length > 0) {
                    builder.addPropertyValue("locations", locations);
                }
                registry.registerBeanDefinition("customPropertySourcesPlaceholderConfigurer", builder.getBeanDefinition());
            }
            {
                // initdefaults 不要忘了正主
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(InitDefaults.class);
                builder.setLazyInit(false);
                registry.registerBeanDefinition(InitDefaults.class.getName(), builder.getBeanDefinition());
            }
            InitDefaultsParser.initBeans(new Defaults(importingClassMetadata), registry);
        }
    }
    
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[] {DefaultsConfiguration.class.getName()};
    }
}
