package com.yoloho.spring.xml;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.yoloho.spring.support.CommonBeanPostProcessor;
import com.yoloho.spring.support.CustomExceptionHandler;

/**
 * 默认WebMvc配置的spring-boot支持
 * 
 * @author jason
 *
 */
@EnableWebMvc
@Configuration
public class BaseWebMvcConfigurer implements WebMvcConfigurer {
    @Bean
    public CustomExceptionHandler dayimaExceptionHandler() {
        // 载入异常捕获器
        CustomExceptionHandler dayimaExceptionHandler = new CustomExceptionHandler();
        return dayimaExceptionHandler;
    }
    
    @Bean
    public CommonBeanPostProcessor commonBeanPostProcessor() {
        // 载入通用参数处理器等
        return new CommonBeanPostProcessor();
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        // 启用默认静态资源处理
        configurer.enable();
    }
}
