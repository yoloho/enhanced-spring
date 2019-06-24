package com.yoloho.enhanced.spring.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.yoloho.enhanced.spring.util.CustomPropertyPlaceholderConfigurer;
import com.yoloho.enhanced.spring.util.CustomPropertySourcesPlaceholderConfigurer;

public class CustomPropertyPlaceholderParser extends AbstractSingleBeanDefinitionParser {
    final static private Logger logger = LoggerFactory.getLogger(CustomPropertyPlaceholderParser.class.getSimpleName());
    
    private static final String SYSTEM_PROPERTIES_MODE_ATTRIBUTE = "system-properties-mode";
    private static final String SYSTEM_PROPERTIES_MODE_DEFAULT = "ENVIRONMENT";
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class getBeanClass(Element element) {
        if (SYSTEM_PROPERTIES_MODE_DEFAULT.equals(element.getAttribute(SYSTEM_PROPERTIES_MODE_ATTRIBUTE))) {
            return CustomPropertySourcesPlaceholderConfigurer.class;
        }
        return CustomPropertyPlaceholderConfigurer.class;
    }
    
    @Override
    protected boolean shouldGenerateId() {
        return true;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        logger.info("init CustomPropertyPlaceholder mode: {}", element.getAttribute(SYSTEM_PROPERTIES_MODE_ATTRIBUTE));
        String location = element.getAttribute("location");
        builder.setLazyInit(false);
        if (StringUtils.hasLength(location)) {
            location = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(location);
            String[] locations = StringUtils.commaDelimitedListToStringArray(location);
            builder.addPropertyValue("locations", locations);
        }

        String propertiesRef = element.getAttribute("properties-ref");
        if (StringUtils.hasLength(propertiesRef)) {
            builder.addPropertyReference("properties", propertiesRef);
        }

        String fileEncoding = element.getAttribute("file-encoding");
        if (StringUtils.hasLength(fileEncoding)) {
            builder.addPropertyValue("fileEncoding", fileEncoding);
        }

        String order = element.getAttribute("order");
        if (StringUtils.hasLength(order)) {
            builder.addPropertyValue("order", Integer.valueOf(order));
        }

        builder.addPropertyValue("ignoreResourceNotFound",
                Boolean.valueOf(element.getAttribute("ignore-resource-not-found")));

        String systemPropertiesModeName = element.getAttribute(SYSTEM_PROPERTIES_MODE_ATTRIBUTE);
        if (StringUtils.hasLength(systemPropertiesModeName) &&
                !systemPropertiesModeName.equals(SYSTEM_PROPERTIES_MODE_DEFAULT)) {
            builder.addPropertyValue("systemPropertiesModeName", "SYSTEM_PROPERTIES_MODE_" + systemPropertiesModeName);
        }
        
        builder.addPropertyValue("localOverride",
                Boolean.valueOf(element.getAttribute("local-override")));

        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        builder.addPropertyValue("ignoreUnresolvablePlaceholders",
                Boolean.valueOf(element.getAttribute("ignore-unresolvable")));
        if (element.hasAttribute("value-separator")) {
            builder.addPropertyValue("valueSeparator", element.getAttribute("value-separator"));
        }
        if (element.hasAttribute("trim-values")) {
            builder.addPropertyValue("trimValues", element.getAttribute("trim-values"));
        }
        if (element.hasAttribute("null-value")) {
            builder.addPropertyValue("nullValue", element.getAttribute("null-value"));
        }
    }
}
