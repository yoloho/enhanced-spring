package com.yoloho.spring.support;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.util.StringUtil;

/**
 * A writable {@code DataSource} with ZooKeeper backend.
 *
 * @author jason
 */
class ZookeeperWritableDataSource<T> implements WritableDataSource<T> {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperWritableDataSource.class.getSimpleName());
    
    private static final int RETRY_TIMES = 3;
    private static final int SLEEP_TIME = 1000;
    
    private final String path;

    private CuratorFramework zkClient = null;
    private final Converter<T, String> parser;

    public ZookeeperWritableDataSource(final String serverAddr, final String path, Converter<T, String> parser) {
        if (StringUtil.isBlank(serverAddr) || StringUtil.isBlank(path)) {
            throw new IllegalArgumentException(String.format("Bad argument: serverAddr=[%s], path=[%s]", serverAddr, path));
        }
        this.path = path;
        this.parser = parser;
        init(serverAddr);
    }

    /**
     * This constructor is Nacos-style.
     */
    public ZookeeperWritableDataSource(final String serverAddr, final String groupId, final String dataId,
                               Converter<T, String> parser) {
        if (StringUtil.isBlank(serverAddr) || StringUtil.isBlank(groupId) || StringUtil.isBlank(dataId)) {
            throw new IllegalArgumentException(String.format("Bad argument: serverAddr=[%s], groupId=[%s], dataId=[%s]", serverAddr, groupId, dataId));
        }
        this.path = getPath(groupId, dataId);
        this.parser = parser;
        init(serverAddr);
        logger.info("init zookeeper writer: serverAddr={}, groupId={}, dataId={}", serverAddr, groupId, dataId);
    }
    
    private void init(String serverAddr) {
        this.zkClient = CuratorFrameworkFactory.newClient(serverAddr, new ExponentialBackoffRetry(SLEEP_TIME, RETRY_TIMES));
        this.zkClient.start();
        try {
            Stat stat = this.zkClient.checkExists().forPath(this.path);
            if (stat == null) {
                this.zkClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(this.path, null);
            }
        } catch (Exception e) {
            logger.warn("[ZookeeperWritableDataSource] Error occurred when initializing Zookeeper data source", e);
        }
    }
    
    private String getPath(String groupId, String dataId) {
        return String.format("/%s/%s", groupId, dataId);
    }

    @Override
    public void close() throws Exception {
        if (this.zkClient != null) {
            this.zkClient.close();
        }
    }

    @Override
    public void write(T value) throws Exception {
        if (this.zkClient == null) {
            throw new IllegalStateException("Zookeeper has not been initialized or error occurred");
        }
        String newVal = this.parser.convert(value);
        //check if has changes
        {
            logger.info("received new val: {}", newVal);
            byte[] data = this.zkClient.getData().forPath(this.path);
            if (data != null) {
                String oldVal = new String(data);
                if (oldVal.equals(newVal)) {
                    logger.info("skip sentinel rules updating(no change)");
                    return;
                }
            }
        }
        this.zkClient.setData().forPath(this.path, newVal.getBytes());
        logger.info("new value updated");
    }
}
