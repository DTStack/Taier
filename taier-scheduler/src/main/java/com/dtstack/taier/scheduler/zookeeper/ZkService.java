/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.scheduler.zookeeper;

import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.LockServiceException;
import com.dtstack.taier.common.exception.LockTimeoutException;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.pluginapi.leader.LeaderNode;
import com.dtstack.taier.pluginapi.leader.LockService;
import com.dtstack.taier.scheduler.server.FailoverStrategy;
import com.dtstack.taier.scheduler.server.listener.HeartBeatCheckListener;
import com.dtstack.taier.scheduler.server.listener.HeartBeatListener;
import com.dtstack.taier.scheduler.server.listener.Listener;
import com.dtstack.taier.scheduler.server.listener.MasterListener;
import com.dtstack.taier.scheduler.utils.PathUtil;
import com.dtstack.taier.scheduler.zookeeper.data.BrokerHeartNode;
import com.dtstack.taier.scheduler.zookeeper.data.BrokersNode;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Component
public class ZkService implements InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkService.class);

    private final static Integer LOCK_WAIT_SECONDS = 5;
    private final static String HEART_NODE = "heart";
    private final static String WORKER_NODE = "workers";
    private final static String LOCK_NODE = "locks";

    private ZkConfig zkConfig;
    private String zkAddress;
    private String localAddress;
    private String distributeRootNode;
    private String brokersNode;
    private String localNode;
    private String workersNode;
    private String lockNode;

    private CuratorFramework zkClient;
    private static ObjectMapper objectMapper = new ObjectMapper();

    private final String appPath = "taier";

    /**
     * when normal stopped，need trigger Listener close();
     */
    private List<Listener> listeners = Lists.newArrayList();

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private FailoverStrategy failoverStrategy;

    private static class LockServiceImpl implements LockService {

        private final CuratorFramework zkClient;
        private final String lockNode;
        private final Map<String, InterProcessMutex> mutexes = new HashMap<>();

        private LockServiceImpl(CuratorFramework zkClient, String lockNode) {
            this.zkClient = zkClient;
            this.lockNode = lockNode;
        }

        @Override
        public void execWithLock(String lockName, Runnable runnable) {
            try {
                boolean locked = tryLock(lockName, LOCK_WAIT_SECONDS, TimeUnit.SECONDS);
                if (locked) {
                    runnable.run();
                } else {
                    throw new LockTimeoutException("Lock " + lockName + " timeout.");
                }
            } finally {
                release(lockName);
            }
        }

        @Override
        public synchronized boolean tryLock(String lockName, int time, TimeUnit timeUnit) {
            InterProcessMutex mutex = this.mutexes.computeIfAbsent(lockName,
                    ln -> new InterProcessMutex(zkClient, String.format("%s/%s", this.lockNode, ln)));
            try {
                return mutex.acquire(time, timeUnit);
            } catch (Exception e) {
                throw new LockServiceException("ZK errors, connection interruptions");
            }
        }

        @Override
        public synchronized void release(String lockName) {
            InterProcessMutex mutex = this.mutexes.get(lockName);
            try {
                if (mutex != null) {
                    mutex.release();
                }
            } catch (Exception e) {
                LOGGER.warn("Couldn't release lock " + lockName);
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Initializing " + this.getClass().getName());

        initConfig();
        checkDistributedConfig();
        initClient();
        zkRegistration();

        LeaderNode.getInstance().setLockService(new LockServiceImpl(zkClient, lockNode));
        LeaderNode.getInstance().finishInit();
    }

    private void initConfig() {
        ZkConfig zkConfig = new ZkConfig();
        zkConfig.setNodeZkAddress(environmentContext.getNodeZkAddress());
        zkConfig.setLocalAddress(environmentContext.getLocalAddress());
        zkConfig.setSecurity(environmentContext.getSecurity());
        this.zkConfig = zkConfig;
    }

    private void initClient() {
        this.zkClient = CuratorFrameworkFactory.builder()
                .connectString(this.zkAddress).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(3000)
                .sessionTimeoutMs(30000).build();
        this.zkClient.start();
        LOGGER.warn("connector zk success...");
    }

    private void zkRegistration() throws Exception {
        createNodeIfNotExists(this.distributeRootNode, "");
        createNodeIfNotExists(this.brokersNode, BrokersNode.initBrokersNode());
        createNodeIfNotExists(this.localNode, "");
        createNodeIfNotExists(this.workersNode, new HashSet<>());
        // 初始化分布式锁节点
        createNodeIfNotExists(this.lockNode, null);
        createLocalBrokerHeartNode();
        initScheduledExecutorService();
        LOGGER.warn("init zk server success...");
    }

    private void initScheduledExecutorService() throws Exception {
        listeners.add(new HeartBeatListener(this));
        String latchPath = String.format("%s/%s", this.distributeRootNode, "masterLatchLock");
        MasterListener masterListener = new MasterListener(failoverStrategy, zkClient, latchPath, localAddress);
        listeners.add(masterListener);
        listeners.add(new HeartBeatCheckListener(masterListener, failoverStrategy, this));
    }

    private void createLocalBrokerHeartNode() throws Exception {
        String node = String.format("%s/%s", this.localNode, HEART_NODE);
        if (zkClient.checkExists().forPath(node) == null) {
            zkClient.create().forPath(node,
                    objectMapper.writeValueAsBytes(BrokerHeartNode.initBrokerHeartNode()));
        } else {
            updateSynchronizedLocalBrokerHeartNode(this.localAddress, BrokerHeartNode.initBrokerHeartNode(), true);
        }
    }

    public void updateSynchronizedLocalBrokerHeartNode(String localAddress, BrokerHeartNode source, boolean isCover) {
        String nodePath = String.format("%s/%s/%s", brokersNode, localAddress, HEART_NODE);
        try {
            BrokerHeartNode target = objectMapper.readValue(zkClient.getData().forPath(nodePath), BrokerHeartNode.class);
            BrokerHeartNode.copy(source, target, isCover);
            zkClient.setData().forPath(nodePath,
                    objectMapper.writeValueAsBytes(target));
        } catch (Exception e) {
            LOGGER.error("{}:updateSynchronizedBrokerHeartNode error:", nodePath, e);
        }
    }

    public void createNodeIfNotExists(String node, Object obj) throws Exception {
        if (zkClient.checkExists().forPath(node) == null) {
            if (obj != null) {
                zkClient.create().forPath(node,
                        objectMapper.writeValueAsBytes(obj));
            } else {
                zkClient.create().forPath(node);
            }
        }
    }

    private void checkDistributedConfig() throws Exception {
        if (StringUtils.isBlank(this.zkConfig.getNodeZkAddress())
                || this.zkConfig.getNodeZkAddress().split("/").length < 2) {
            throw new RdosDefineException("zkAddress is error");
        }
        String[] zks = this.zkConfig.getNodeZkAddress().split("/");
        this.zkAddress = zks[0].trim();
        this.distributeRootNode = String.format("/%s", zks[1].trim());
        this.localAddress = zkConfig.getLocalAddress();
        if (StringUtils.isBlank(this.localAddress) || this.localAddress.split(":").length < 2) {
            throw new RdosDefineException("localAddress is error");
        }
        this.brokersNode = String.format("%s/brokers", this.distributeRootNode);
        this.localNode = String.format("%s/%s", this.brokersNode, this.localAddress);
        this.workersNode = String.format("%s/%s", this.localNode, WORKER_NODE);
        // 初始化分布式锁节点名称
        this.lockNode = String.format("%s/%s", this.distributeRootNode, LOCK_NODE);
    }

    public BrokerHeartNode getBrokerHeartNode(String node) {
        try {
            String nodePath = String.format("%s/%s/%s", this.brokersNode, node, HEART_NODE);
            return objectMapper.readValue(zkClient.getData()
                    .forPath(nodePath), BrokerHeartNode.class);
        } catch (Exception e) {
            LOGGER.error("{}:getBrokerHeartNode error:", node, e);
        }
        return BrokerHeartNode.initNullBrokerHeartNode();
    }

    public List<String> getBrokersChildren() {
        try {
            return zkClient.getChildren().forPath(this.brokersNode);
        } catch (Exception e) {
            LOGGER.error("getBrokersChildren error:", e);
        }
        return Lists.newArrayList();
    }

    public List<String> getAliveBrokersChildren() {
        List<String> alives = Lists.newArrayList();
        try {
            if (null != zkClient) {
                List<String> brokers = zkClient.getChildren().forPath(this.brokersNode);
                for (String broker : brokers) {
                    BrokerHeartNode brokerHeartNode = getBrokerHeartNode(broker);
                    if (brokerHeartNode.getAlive()) {
                        alives.add(broker);
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("getBrokersChildren error:", e);
        }
        return alives;
    }

    public List<Map<String, Object>> getAllBrokerWorkersNode() {
        List<Map<String, Object>> allWorkers = new ArrayList<>();
        List<String> children = this.getBrokersChildren();
        for (String address : children) {
            String nodePath = String.format("%s/%s/%s", this.brokersNode, address, WORKER_NODE);
            try {
                List<Map<String, Object>> workerNode = objectMapper.readValue(zkClient.getData().forPath(nodePath), ArrayList.class);
                allWorkers.addAll(workerNode);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return allWorkers;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void disableBrokerHeartNode(String localAddress, boolean stopHealthCheck) {
        BrokerHeartNode disableBrokerHeartNode = BrokerHeartNode.initNullBrokerHeartNode();
        if (stopHealthCheck) {
            disableBrokerHeartNode.setSeq(HeartBeatCheckListener.getStopHealthCheckSeq());
        }
        this.updateSynchronizedLocalBrokerHeartNode(localAddress, disableBrokerHeartNode, true);
    }

    @Override
    public void destroy() throws Exception {
        disableBrokerHeartNode(this.localAddress, false);
        for (Listener listener : listeners) {
            try {
                listener.close();
                LOGGER.info("close {}", listener.getClass().getSimpleName());
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    /**
     * 设置 watcher
     * @param group 分组
     * @param key key
     * @param curatorWatcher watcher 回调
     */
    public void setWatcher(String group, String key, CuratorWatcher curatorWatcher) {
        String path = PathUtil.getPath(appPath, group, key);
        try {
            Stat stat = zkClient.checkExists().forPath(path);
            if (stat == null) {
                // 递归创建节点
                zkClient.create().creatingParentsIfNeeded().forPath(path);
            }
            zkClient.checkExists().usingWatcher(curatorWatcher).forPath(path);
        } catch (Exception e) {
            LOGGER.error("set watcher fail, path:{}", path, e);
        }
    }

    /**
     * 删除节点
     *
     * @param group 分组
     * @param key   key
     */
    public void delete(String group, String key) {
        String path = PathUtil.getPath(appPath, group, key);
        try {
            Stat stat = zkClient.checkExists().forPath(path);
            if (stat != null) {
                zkClient.delete().guaranteed().forPath(path);
            }
        } catch (Exception e) {
            LOGGER.error("delete zNode fail, path:{}", path, e);
        }
    }

    /**
     * 删除组节点
     *
     * @param group 分组
     */
    public void deleteGroup(String group) {
        String path = PathUtil.getPath(appPath, group);
        try {
            Stat stat = zkClient.checkExists().forPath(path);
            if (stat != null) {
                // 递归删除节点
                zkClient.delete().deletingChildrenIfNeeded().forPath(path);
            }
        } catch (Exception e) {
            LOGGER.error("delete group zNode fail, path:{}", path, e);
        }
    }

    public void setEnvironmentContext(EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }
}
