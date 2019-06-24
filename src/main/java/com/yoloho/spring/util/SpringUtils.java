package com.yoloho.spring.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Store the context for future use
 * 
 * @author qianxg
 *
 */
public class SpringUtils {
	
	private static ApplicationContext appContext;
	
	public static void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		appContext = applicationContext;
	}
	
	/**
	 * Proxy the getBean method
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz) {
		return appContext.getBean(clazz);
	}

	/**
	 * Proxy the getBean method
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz, String name) {
		return appContext.getBean(name, clazz);
	}
	
	/**
	 * Proxy the getBean method
	 * 
	 * @param name
	 * @return
	 */
	public static Object getBean(String name) {
		return appContext.getBean(name);
	}
	
	public static ApplicationContext getAppContext() {
        return appContext;
    }
	
	/**
	 * Proxy the getBeansOfType method
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
		return appContext.getBeansOfType(clazz);
	}
	
}
