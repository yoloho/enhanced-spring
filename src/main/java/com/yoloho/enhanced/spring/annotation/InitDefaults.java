package com.yoloho.enhanced.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.yoloho.enhanced.spring.xml.InitDefaultsConfiguration;

/**
 * Some scaffold functions
 * 
 * @author jason
 *
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(InitDefaultsConfiguration.class)
public @interface InitDefaults {
    /**
     * Whether output the log into console(stdout)
     * Another property option is "log.console=true"
     * 
     * @return
     */
    boolean logConsole() default true;
    /**
     * Whether output the log into files (app.log and error.log)
     * Another property option is "log.file=true"
     * 
     * @return
     */
    boolean logFile() default true;
    
    /**
     * logging level
     * <p>
     * Default to be "INFO"
     * 
     * @return
     */
    String logLevel() default "INFO";
    
    /**
     * Used in sentinel to identify name of project
     * 
     * @return
     */
    String projectName() default "";
    
    /**
     * Which port sentinel-transport will behind
     * 
     * @return
     */
    int sentinelPort() default 0;
    
    /**
     * Optional. Group name of datasource.
     * 
     * @return
     */
    String sentinelGroup() default "";
    
    /**
     * Whether to capture normal type request (non-json).
     * <p>
     * Default to false.
     * 
     * @return
     */
    boolean captureNormalRequest() default false;
    
    /**
     * Whether taking all request as json type requests.
     * <p>
     * Default to false.
     * 
     * @return
     */
    boolean takeAllRequestsAsJson() default false;
    
    /**
     * When exception is occurred in normal request whether using a common message as response.
     * <p>
     * Default to show detail error message.
     * 
     * @return
     */
    String errorMessageForNormalRequest() default "";
    
    /**
     * When exception is occurred in normal request whether using a common message as response(JSON response).
     * <p>
     * Default to show detail error message.
     * 
     * @return
     */
    String errorMessageForJSONRequest() default "";
    
    /**
     * ".roperties" file list separated by comma to load for placeholders.
     * <p>
     * eg. "conf/system.properties,conf/redis.properties"
     * 
     * @return
     */
    String[] propertyLocations() default {};
}
