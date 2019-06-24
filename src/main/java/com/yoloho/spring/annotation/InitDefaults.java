package com.yoloho.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.yoloho.spring.xml.InitDefaultsConfiguration;

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
    
    String logLevel() default "INFO";
    String projectName() default "";
    int sentinelPort() default 0;
    String sentinelGroup() default "";
    boolean captureNormalRequest() default false;
    boolean takeAllRequestsAsJson() default false;
    String errorMessageForNormalRequest() default "";
    String[] propertyLocations() default {};
}
