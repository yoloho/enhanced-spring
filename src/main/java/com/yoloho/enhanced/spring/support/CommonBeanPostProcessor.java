package com.yoloho.enhanced.spring.support;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.collect.Lists;
import com.yoloho.enhanced.spring.config.MethodArgumentResolver;

/**
 * Post processor of beans for customized argument resolvers and message converters
 * 
 * @author jason
 *
 */
public class CommonBeanPostProcessor implements BeanPostProcessor {
    private static Logger logger = LoggerFactory.getLogger(CommonBeanPostProcessor.class.getSimpleName());

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
            // argumentResolver
            RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
            
            this.appendCustomResolver(adapter);
            
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(2);
            messageConverters.add(new ByteArrayHttpMessageConverter());
            messageConverters.add(new StringHttpMessageConverter(Charset.forName("utf-8")));
            FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
            {
                fastJsonConverter.setSupportedMediaTypes(MediaType.parseMediaTypes(
                        Lists.newArrayList(
                                "application/json;charset=UTF-8", 
                                "application/*+json;charset=UTF-8",
                                "text/json;charset=UTF-8", 
                                "text/plain;charset=UTF-8", 
                                "text/html;charset=UTF-8"
                                )));
                FastJsonConfig fastJsonConfig = new FastJsonConfig();
                fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat,
                        SerializerFeature.QuoteFieldNames, SerializerFeature.DisableCircularReferenceDetect);
                fastJsonConverter.setFastJsonConfig(fastJsonConfig);
            }
            messageConverters.add(fastJsonConverter);
            adapter.setMessageConverters(messageConverters);
            logger.info("Initialize message converters");
        }
        
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
    
    /**
     * 检查并添加BaseVo请求解析器
     * 
     * @param adapter
     */
    private void appendCustomResolver(RequestMappingHandlerAdapter adapter) {
        List<HandlerMethodArgumentResolver> resolvers = adapter.getCustomArgumentResolvers();
        boolean isLoaded = false;
        Set<Class<?>> resolverClazzSet = new HashSet<>();
        if (resolvers != null) {
            for (HandlerMethodArgumentResolver curResolver : resolvers) {
                if (curResolver instanceof MethodArgumentResolver) {
                    isLoaded = true;
                }
                resolverClazzSet.add(curResolver.getClass());
            }
        } else {
            resolvers = Lists.newArrayList();
        }
        if (!isLoaded) {
            resolvers.add(new MethodArgumentResolver());
            resolverClazzSet.add(MethodArgumentResolver.class);
        }
        adapter.setCustomArgumentResolvers(resolvers);
        logger.info("Custom SpringMVC argument resolver loaded：{}", JSON.toJSONString(resolverClazzSet));
    }

}