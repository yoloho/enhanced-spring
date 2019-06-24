package com.yoloho.spring.support;

import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.PostConstruct;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.zookeeper.ZookeeperDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * readable data source of zkServer initializer
 * 
 * @author jason
 *
 */
public class SentinelZookeeperDataSourceInit extends SentinelZookeeperDataSource {

    private static final Type flowRuleListType = new TypeReference<List<FlowRule>>() {}.getType();
    private static final Type degradeRuleListType = new TypeReference<List<DegradeRule>>() {}.getType();
    private static final Type systemRuleListType = new TypeReference<List<SystemRule>>() {}.getType();
    
    public SentinelZookeeperDataSourceInit(String group, String name) {
        super(group, name);
    }
    
    @PostConstruct
    @Override
    public void init() {
        String zkServer = getZkServer();
        
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new ZookeeperDataSource<>(zkServer, getGroup(),
                getName() + "/flow", new Converter<String, List<FlowRule>>() {

                    @Override
                    public List<FlowRule> convert(String source) {
                        return JSON.parseObject(source, flowRuleListType);
                    }
                });
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());

        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new ZookeeperDataSource<>(zkServer,
                getGroup(), getName() + "/degrade", new Converter<String, List<DegradeRule>>() {

                    @Override
                    public List<DegradeRule> convert(String source) {
                        return JSON.parseObject(source, degradeRuleListType);
                    }
                });
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());

        ReadableDataSource<String, List<SystemRule>> systemRuleDataSource = new ZookeeperDataSource<>(zkServer,
                getGroup(), getName() + "/system", new Converter<String, List<SystemRule>>() {

                    @Override
                    public List<SystemRule> convert(String source) {
                        return JSON.parseObject(source, systemRuleListType);
                    }
                });
        SystemRuleManager.register2Property(systemRuleDataSource.getProperty());
        
    }
}
