package com.yoloho.enhanced.spring.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.yoloho.enhanced.spring.support.MvcArgument;

public class MethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
    	Class<?> beanType = parameter.getParameterType();
    	return MvcArgument.class.isAssignableFrom(beanType);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    	Class<?> beanType = parameter.getParameterType();
		if (MvcArgument.class.isAssignableFrom(beanType)) {
		    Constructor<?> constructor = beanType.getDeclaredConstructor();
		    boolean visible = true;
		    if (!constructor.isAccessible()) {
		        visible = false;
		        constructor.setAccessible(true);
		    }
		    MvcArgument mvcArgument = (MvcArgument) constructor.newInstance();
		    if (!visible) {
		        constructor.setAccessible(false);
		    }
		    return mvcArgument.init(getRequest());
		}
		return null;
    }
    
    private static final String CLASS_HOLDER = "org.springframework.web.context.request.RequestContextHolder";
    private static final String CLASS_REQUEST_ATTRS = "org.springframework.web.context.request.ServletRequestAttributes";
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private javax.servlet.http.HttpServletRequest getRequest() {
        try {
            Class cls_holder = Class.forName(CLASS_HOLDER);
            Class.forName(CLASS_REQUEST_ATTRS);
            Method method = cls_holder.getMethod("getRequestAttributes");
            Object servletRequestAttributes = method.invoke(cls_holder);
            Method methodRequest = servletRequestAttributes.getClass().getMethod("getRequest");
            return (javax.servlet.http.HttpServletRequest) methodRequest.invoke(servletRequestAttributes);
        } catch (Exception e) {
        }
        return null;
    }
}