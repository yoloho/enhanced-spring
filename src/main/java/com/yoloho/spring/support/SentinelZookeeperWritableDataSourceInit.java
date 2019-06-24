package com.yoloho.spring.support;

import java.util.List;

import javax.annotation.PostConstruct;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;

/**
 * writable data source of zkServer initializer
 * 
 * @author jason
 *
 */
public class SentinelZookeeperWritableDataSourceInit extends SentinelZookeeperDataSource {
    
    public SentinelZookeeperWritableDataSourceInit(String group, String name) {
        super(group, name);
    }
    
    @PostConstruct
    @Override
    public void init() {
        String remoteAddress = getZkServer();
        
        WritableDataSource<List<FlowRule>> flowRuleDataSource = new ZookeeperWritableDataSource<>(remoteAddress, getGroup(),
                getName() + "/flow", new Converter<List<FlowRule>, String>() {

                    @Override
                    public String convert(List<FlowRule> rules) {
                        return JSON.toJSONString(rules);
                    }
                });
        WritableDataSourceRegistry.registerFlowDataSource(flowRuleDataSource);

        WritableDataSource<List<DegradeRule>> degradeRuleDataSource = new ZookeeperWritableDataSource<>(remoteAddress,
                getGroup(), getName() + "/degrade", new Converter<List<DegradeRule>, String>() {

                    @Override
                    public String convert(List<DegradeRule> rules) {
                        return JSON.toJSONString(rules);
                    }
                });
        WritableDataSourceRegistry.registerDegradeDataSource(degradeRuleDataSource);

        WritableDataSource<List<SystemRule>> systemRuleDataSource = new ZookeeperWritableDataSource<>(remoteAddress,
                getGroup(), getName() + "/system", new Converter<List<SystemRule>, String>() {

                    @Override
                    public String convert(List<SystemRule> rules) {
                        return JSON.toJSONString(rules);
                    }
                });
        WritableDataSourceRegistry.registerSystemDataSource(systemRuleDataSource);
        
    }
}
