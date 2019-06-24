package com.yoloho.enhanced.spring.util;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.yoloho.enhanced.common.util.Logging;

public class InitDefaults implements ApplicationContextAware {
    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(InitDefaults.class.getSimpleName());
    
    public static void setLoggingLevel(String level) {
        Logging.setLoggingLevel(level);
    }
    
    public static void initLogging() {
        initLogging(true);
    }
    
    public static void initLogging(boolean console) {
        Logging.initLogging(console);
    }
    
    public static void initLogging(boolean console, boolean file) {
        Logging.initLogging(console, file);
    }
    
    private void reinitLogging() {
        //做后期日志适配
        //适配顺序tag property(不支持变量) -> system property -> properties file
        //逐级覆盖
        boolean needReinit = false;
        boolean console = true;
        boolean file = true;
        String logConsole = PropertyUtil.getProperty("log.console");
        String logFile = PropertyUtil.getProperty("log.file");
        if (StringUtils.isNotEmpty(logConsole)) {
            needReinit = true;
            console = BooleanUtils.toBoolean(logConsole);
        }
        if (StringUtils.isNotEmpty(logFile)) {
            needReinit = true;
            file = BooleanUtils.toBoolean(logFile);
        }
        if (needReinit) {
            Logging.initLogging(console, file);
        }
    }
    
    public InitDefaults() {
        //other inits
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.setApplicationContext(applicationContext);
        reinitLogging();
    }
}
