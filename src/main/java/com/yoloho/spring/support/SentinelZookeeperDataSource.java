package com.yoloho.spring.support;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;

import com.yoloho.spring.util.PropertyUtil;

public abstract class SentinelZookeeperDataSource {
    private String group;
    private String name;

    public SentinelZookeeperDataSource(String group, String name) {
        if (StringUtils.isEmpty(group)) {
            group = "sentinel";
        }
        if (StringUtils.isEmpty(name)) {
            throw new RuntimeException("init-default -> project-name must not be empty");
        }
        this.group = group;
        this.name = name;
    }
    
    protected String getZkServer() {
        String remoteAddress = PropertyUtil.getProperty("sentinel.zookeeper");
        if (StringUtils.isEmpty(remoteAddress)) {
            // 拿dubbo的zkServer作为fallback
            remoteAddress = PropertyUtil.getProperty("dubbo.registry.url");
        }
        if (StringUtils.isEmpty(remoteAddress)) {
            throw new RuntimeException("sentinel zookeeper addr is empty");
        }
        return remoteAddress;
    }
    
    public String getGroup() {
        return group;
    }
    
    public String getName() {
        return name;
    }
    
    @PostConstruct
    public abstract void init();
}
