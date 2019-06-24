package com.yoloho.enhanced.spring.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("init", new InitDefaultsParser());
        registerBeanDefinitionParser("property-placeholder", new CustomPropertyPlaceholderParser());
    }

}
