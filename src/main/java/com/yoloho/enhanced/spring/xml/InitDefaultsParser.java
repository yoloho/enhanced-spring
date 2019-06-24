package com.yoloho.enhanced.spring.xml;

import java.lang.reflect.Method;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.yoloho.enhanced.common.annotation.Nullable;
import com.yoloho.enhanced.spring.support.CommonBeanPostProcessor;
import com.yoloho.enhanced.spring.support.CustomExceptionHandler;
import com.yoloho.enhanced.spring.support.SentinelUrlBlockHandlerInit;
import com.yoloho.enhanced.spring.support.SentinelZookeeperDataSourceInit;
import com.yoloho.enhanced.spring.support.SentinelZookeeperWritableDataSourceInit;
import com.yoloho.enhanced.spring.util.InitDefaults;

public class InitDefaultsParser extends AbstractSimpleBeanDefinitionParser {
    private final static Logger logger = LoggerFactory.getLogger(InitDefaultsParser.class.getSimpleName());
    protected final static String LOG_CONSOLE = "log.console";
    protected final static String LOG_FILE = "log.file";
    
    private static Defaults defaults = new Defaults();
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class getBeanClass(Element element) {
        return InitDefaults.class;
    }

    @Override
    protected boolean shouldGenerateId() {
        return true;
    }
    
    /**
     * init-defaults标签中的项目名，未设置则为null
     * 
     * @return
     */
    @Nullable
    public static String getProjectName() {
        return defaults.getProjectName();
    }
    
    /**
     * 哨兵sentinel组名字
     * 
     * @return
     */
    public static String getGroupName() {
        return defaults.getSentinelGroup();
    }
    
    /**
     * init-defaults标签中的哨兵端口设置，不设置则使用默认端口顺排
     * 
     * @return
     */
    @Nullable
    public static int getSentinelPort() {
        return defaults.getSentinelPort();
    }
    
    /**
     * 这里为了支持spring-boot重用逻辑，先暂时放为静态方法
     * 
     * @param registry
     */
    protected static void initBeans(Defaults defaults, BeanDefinitionRegistry registry) {
        InitDefaultsParser.defaults = defaults;
        {
            if (StringUtils.isNotEmpty(defaults.getLogLevel())) {
                InitDefaults.setLoggingLevel(defaults.getLogLevel());
            }
            //logging
            {
                boolean logToConsole = defaults.isLogConsole();
                boolean logToFile = defaults.isLogFile();
                //system properties覆盖
                if (StringUtils.isNotEmpty(System.getProperty(LOG_CONSOLE))) {
                    logToConsole = BooleanUtils.toBoolean(System.getProperty(LOG_CONSOLE));
                }
                if (StringUtils.isNotEmpty(System.getProperty(LOG_FILE))) {
                    logToFile = BooleanUtils.toBoolean(System.getProperty(LOG_FILE));
                }
                InitDefaults.initLogging(logToConsole, logToFile);
            }
        }
        {
            // 判断是否引用了sentinel-web-servlet，设置url阻断spi
            {
                String className = "com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler";
                try {
                    Class<?> clz = Class.forName(className);
                    if (clz != null) {
                        //init zookeeper datasource
                        BeanDefinitionBuilder sentinelUrlBlockHandlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(SentinelUrlBlockHandlerInit.class);
                        sentinelUrlBlockHandlerBuilder.setLazyInit(false);
                        registry.registerBeanDefinition("sentinelUrlBlockHandlerBuilder", sentinelUrlBlockHandlerBuilder.getBeanDefinition());
                        logger.info("inject sentinel url block handler init bean");
                    }
                } catch (Exception e) {
                }
            }
            boolean hasZookeeperDataSource = false;
            {
                //zookeeper readable datasource
                String className = "com.alibaba.csp.sentinel.datasource.zookeeper.ZookeeperDataSource";
                try {
                    Class<?> clz = Class.forName(className);
                    if (clz != null) {
                        //init zookeeper datasource
                        BeanDefinitionBuilder sentinelZookeeperDSBuilder = BeanDefinitionBuilder.genericBeanDefinition(SentinelZookeeperDataSourceInit.class);
                        sentinelZookeeperDSBuilder.addConstructorArgValue(getGroupName());
                        sentinelZookeeperDSBuilder.addConstructorArgValue(getProjectName());
                        sentinelZookeeperDSBuilder.setLazyInit(false);
                        registry.registerBeanDefinition("sentinelZookeeperDSBuilder", sentinelZookeeperDSBuilder.getBeanDefinition());
                        logger.info("inject sentinel zookeeper readable datasource init bean");
                        hasZookeeperDataSource = true;
                    }
                } catch (Exception e) {
                }
            }
            if (hasZookeeperDataSource) {
                //zookeeper writable datasource
                String className = "com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry";
                try {
                    Class<?> clz = Class.forName(className);
                    if (clz != null) {
                        //init zookeeper datasource
                        BeanDefinitionBuilder sentinelZookeeperWDSBuilder = BeanDefinitionBuilder.genericBeanDefinition(SentinelZookeeperWritableDataSourceInit.class);
                        sentinelZookeeperWDSBuilder.addConstructorArgValue(getGroupName());
                        sentinelZookeeperWDSBuilder.addConstructorArgValue(getProjectName());
                        sentinelZookeeperWDSBuilder.setLazyInit(false);
                        registry.registerBeanDefinition("sentinelZookeeperWDSBuilder", sentinelZookeeperWDSBuilder.getBeanDefinition());
                        logger.info("inject sentinel zookeeper writable datasource init bean");
                    }
                } catch (Exception e) {
                }
            }
        }
        // spring-web
        boolean inSpringWeb = false;
        try {
            // 首先检查servlet依赖
            String servletClassName = "javax.servlet.ServletContext";
            Class.forName(servletClassName);
            String className = "org.springframework.web.context.ContextLoader";
            Class<?> clz = Class.forName(className);
            Method method = clz.getMethod("getCurrentWebApplicationContext");
            Object context = method.invoke(clz);
            if (context != null) {
                logger.info("found Servlet instance");
                inSpringWeb = true;
            }
        } catch (Exception e) {
        }
        // 异常处理，这里在spring-boot环境中，将不会有inSpringWeb=true（因为初始化得早）
        // 这时该bean将通过配置类进行注入，所以这里需要做属性设置
        if (defaults.isCaptureNormalRequest()) {
            CustomExceptionHandler.setHandleNormalRequest(true);
        }
        if (defaults.isTakeAllRequestsAsJson()) {
            CustomExceptionHandler.setTakeAllRequestAsJSON(true);
        }
        if (StringUtils.isNotEmpty(defaults.getErrorMessageForNormalRequest())) {
            CustomExceptionHandler.setNormalMsgError(defaults.getErrorMessageForNormalRequest());
        }
        if (inSpringWeb) {
            {
                // 异常处理
                registry.registerBeanDefinition("customExceptionHandler", BeanDefinitionBuilder.genericBeanDefinition(CustomExceptionHandler.class).setLazyInit(false).getBeanDefinition());
            }
            {
                // 请求过滤器及参数解析器
                registry.registerBeanDefinition("commonBeanPostProcessor", BeanDefinitionBuilder.genericBeanDefinition(CommonBeanPostProcessor.class).setLazyInit(false).getBeanDefinition());
            }
        }
    }
    
    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.setLazyInit(false);
        // 注意，这里已经隐含要初始化InitDefaults作为Bean
        InitDefaultsParser.defaults = new Defaults(element);
        initBeans(defaults, parserContext.getRegistry());
    }

}
