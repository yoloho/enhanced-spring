package com.yoloho.enhanced.spring.xml;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.AnnotationMetadata;
import org.w3c.dom.Element;

import com.yoloho.enhanced.spring.annotation.InitDefaults;

class Defaults {
    private static final Logger logger = LoggerFactory.getLogger(Defaults.class.getSimpleName());
    
    private boolean logConsole = true;
    private boolean logFile = true;
    private String logLevel = "INFO";
    private String projectName = null;
    private int sentinelPort = 0;
    private String sentinelGroup = null;
    private boolean captureNormalRequest = false;
    private boolean takeAllRequestsAsJson = false;
    private String errorMessageForNormalRequest = null;
    
    public Defaults() {
    }
    
    public Defaults(AnnotationMetadata annotationMetadata) {
        Map<String, Object> map = annotationMetadata.getAnnotationAttributes(InitDefaults.class.getName());
        if (map == null) {
            logger.warn("no annotation configuration found, using defaults");
            return;
        }
        if (StringUtils.isNotEmpty((String) map.get("logLevel"))) {
            this.logLevel = (String) map.get("logLevel");
        }
        this.logConsole = (Boolean)map.get("logConsole");
        this.logFile = (Boolean)map.get("logFile");
        initSentinel((String)map.get("projectName"), String.valueOf(map.get("sentinelPort")),
                (String)map.get("sentinelGroup"));
        this.captureNormalRequest = (Boolean)map.get("captureNormalRequest");
        this.takeAllRequestsAsJson = (Boolean)map.get("takeAllRequestsAsJson");
        this.errorMessageForNormalRequest = (String)map.get("errorMessageForNormalRequest");
    }
    
    public Defaults(Element element) {
        if (StringUtils.isNotEmpty(element.getAttribute("log-level"))) {
            this.logLevel = element.getAttribute("log-level");
        }
        this.logConsole = !StringUtils.equalsIgnoreCase(element.getAttribute("log-console"), "false");
        initSentinel(element.getAttribute("project-name"), element.getAttribute("sentinel-port"),
                element.getAttribute("sentinel-group"));
        this.captureNormalRequest = StringUtils.equalsIgnoreCase(element.getAttribute("capture-normal-request"), "true");
        this.takeAllRequestsAsJson = StringUtils.equalsIgnoreCase(element.getAttribute("take-all-requests-as-json"), "true");
        this.errorMessageForNormalRequest = element.getAttribute("error-msg-normal-request");
    }
    
    private void initSentinel(String projectName, String sentinelPort, String sentinelGroup) {
        //哨兵sentinel相关设置
        if (StringUtils.isNotEmpty(projectName)) {
            this.projectName = projectName;
            System.setProperty("project.name", projectName);
        }
        if (StringUtils.isNotEmpty(sentinelPort)) {
            this.sentinelPort = NumberUtils.toInt(sentinelPort);
            System.setProperty("csp.sentinel.api.port", sentinelPort);
        }
        if (StringUtils.isNotEmpty(sentinelGroup)) {
            this.sentinelGroup = sentinelGroup;
        }
        //设置环境变量
        {
            String dashboardServer = System.getProperty("sentinel.dashboard");
            if (StringUtils.isEmpty(dashboardServer)) {
                dashboardServer = "sentinel.dayima.org:80";
            }
            System.setProperty("csp.sentinel.dashboard.server", dashboardServer);
            String baseDir = System.getProperty("catalina.base");
            if (StringUtils.isNotEmpty(baseDir)) {
                System.setProperty("user.home", baseDir);
            }
        }
    }

    public boolean isLogConsole() {
        return logConsole;
    }

    public void setLogConsole(boolean logConsole) {
        this.logConsole = logConsole;
    }

    public boolean isLogFile() {
        return logFile;
    }

    public void setLogFile(boolean logFile) {
        this.logFile = logFile;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getSentinelPort() {
        return sentinelPort;
    }

    public void setSentinelPort(int sentinelPort) {
        this.sentinelPort = sentinelPort;
    }

    public String getSentinelGroup() {
        return sentinelGroup;
    }

    public void setSentinelGroup(String sentinelGroup) {
        this.sentinelGroup = sentinelGroup;
    }

    public boolean isCaptureNormalRequest() {
        return captureNormalRequest;
    }

    public void setCaptureNormalRequest(boolean captureNormalRequest) {
        this.captureNormalRequest = captureNormalRequest;
    }

    public boolean isTakeAllRequestsAsJson() {
        return takeAllRequestsAsJson;
    }

    public void setTakeAllRequestsAsJson(boolean takeAllRequestsAsJson) {
        this.takeAllRequestsAsJson = takeAllRequestsAsJson;
    }

    public String getErrorMessageForNormalRequest() {
        return errorMessageForNormalRequest;
    }

    public void setErrorMessageForNormalRequest(String errorMessageForNormalRequest) {
        this.errorMessageForNormalRequest = errorMessageForNormalRequest;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
}
