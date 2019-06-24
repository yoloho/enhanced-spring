package com.yoloho.spring.util;

import java.io.IOException;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;

import com.yoloho.spring.util.PropertyUtil.CustomProperties;

public class CustomPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements CustomProperties {
    final private static Logger logger = LoggerFactory.getLogger(CustomPropertyPlaceholderConfigurer.class.getSimpleName());
    private ConfigurableListableBeanFactory factory;
    
    public CustomPropertyPlaceholderConfigurer() {
        logger.info("create configurer");
        PropertyUtil.addPlaceHolderConfigurer(this);
    }

    @Override
    public void setLocation(Resource location) {
        super.setLocation(ResourceLocationHandler.processLocation(location));
    }
    
    @Override
    protected void loadProperties(Properties props) throws IOException {
        super.loadProperties(props);
        // Check whether in eru
        if (StringUtils.isNotEmpty(System.getenv("ERU_POD"))) {
            // do env override
            for (Entry<String, String> entry : System.getenv().entrySet()) {
                String prop = EnvUtil.parseName(entry.getKey());
                if (prop != null) {
                    logger.info("Do env prop overriding: {}", prop);
                    props.put(prop, entry.getValue());
                    if (prop.startsWith("dubbo.")) {
                        // for dubbo properties, we need put them to System.properties to override dubbo.properties
                        // because dubbo will finally find dubbo.properties file to load properties directly
                        System.setProperty(prop, entry.getValue());
                    }
                }
            }
        }
    }

    public void setLocations(Resource... locations) {
        ResourceLocationHandler.processLocations(locations);
        super.setLocations(locations);
    }
    
    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        this.factory = beanFactoryToProcess;
    }

    @Override
    public String resolveProperty(String name) {
        try {
            return factory.resolveEmbeddedValue(name);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public String getProperty(String name) {
        return resolveProperty(String.format("%s%s%s", this.placeholderPrefix, name, this.placeholderSuffix));
    }
}
