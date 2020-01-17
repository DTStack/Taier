package com.dtstack.task.server.zk;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.ExceptionUtil;
import com.dtstack.engine.common.util.KerberosUtils;
import com.dtstack.task.server.node.MasterNode;
import com.dtstack.task.server.node.WorkNode;
import com.dtstack.task.server.zk.listener.HeartBeatCheckListener;
import com.dtstack.task.server.zk.listener.HeartBeatListener;
import com.dtstack.task.server.zk.listener.Listener;
import com.dtstack.task.server.zk.listener.MasterListener;
import com.dtstack.task.server.zk.listener.QueueListener;
import com.dtstack.task.server.zk.data.BrokerHeartNode;
import com.dtstack.task.server.zk.data.BrokerQueueNode;
import com.dtstack.task.server.zk.data.BrokersNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import com.netflix.curator.retry.ExponentialBackoffRetry;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Component
public class ZkService implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(ZkService.class);

    private final static String META_DATA_NODE = "data";
    private final static String META_DATA_LOCK = "dataLock";
    private final static String HEART_NODE = "heart";
    private final static String QUEUE_NODE = "queue";

    private ZkConfig zkConfig;
    private String zkAddress;
    private String localAddress;
    private String distributeRootNode;
    private String brokersNode;
    private String localNode;

    private CuratorFramework zkClient;
    private InterProcessMutex masterLock;
    private InterProcessMutex brokerHeartLock;
    private InterProcessMutex brokerQueueLock;

    private String lastMasterAddress = "";

    private static ObjectMapper objectMapper = new ObjectMapper();
    private List<InterProcessMutex> interProcessMutexes = Lists.newArrayList();
    private List<Listener> listeners = Lists.newArrayList();

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private MasterNode masterNode;

    @Autowired
    private WorkNode workNode;

    @Override
    public void afterPropertiesSet() throws Exception {
        initConfig();
        checkDistributedConfig();
        initClient();
        zkRegistration();
    }

    private void initConfig() {
        ZkConfig zkConfig = new ZkConfig();
        zkConfig.setNodeZkAddress(environmentContext.getNodeZkAddress());
        zkConfig.setLocalAddress(environmentContext.getLocalAddress());
        zkConfig.setSecurity(environmentContext.getSecurity());
        this.zkConfig = zkConfig;
    }

    private void initClient() {
        if (zkConfig.getSecurity() != null) {
            initSecurity();
        }
        this.zkClient = CuratorFrameworkFactory.builder()
                .connectString(this.zkAddress).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(1000)
                .sessionTimeoutMs(1000).build();
        this.zkClient.start();
        logger.warn("connector zk success...");
    }

    private void initSecurity() {
        try {
            Map<String, String> securityKvs = zkConfig.getSecurity();
            String userPrincipal = securityKvs.get("userPrincipal");
            String userKeytabPath = securityKvs.get("userKeytabPath");
            String krb5ConfPath = securityKvs.get("krb5ConfPath");
            String zkPrincipal = securityKvs.get("zkPrincipal");
            String loginContextName = securityKvs.get("loginContextName");

            KerberosUtils.setJaasConf(loginContextName, userPrincipal, userKeytabPath);
            KerberosUtils.setZookeeperServerPrincipal("zookeeper.server.principal", zkPrincipal);
            Configuration hadoopConf = new Configuration();
            hadoopConf.set("hadoop.security.authentication", "kerberos");
            hadoopConf.setBoolean("hadoop.security.authorization", true);
            KerberosUtils.login(userPrincipal, userKeytabPath, krb5ConfPath, hadoopConf);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    private ZkService zkRegistration() throws Exception {
        createNodeIfNotExists(this.distributeRootNode, "");
        createNodeIfNotExists(this.brokersNode, BrokersNode.initBrokersNode());
        createNodeIfNotExists(this.localNode, "");
        initNeedLock();
        createLocalBrokerHeartNode();
        createLocalBrokerDataNode();
        createLocalBrokerDataLock();
        createLocalBrokerQueueNode();
        initScheduledExecutorService();
        logger.warn("init zk server success...");
        return this;
    }

    private void initScheduledExecutorService() {
        listeners.add(new HeartBeatListener(this));
        MasterListener masterListener = new MasterListener(masterNode, this);
        listeners.add(masterListener);
        listeners.add(new HeartBeatCheckListener(masterListener, masterNode, this));
        listeners.add(new QueueListener(workNode, this));
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

    private void createLocalBrokerDataNode() throws Exception {
        String nodePath = String.format("%s/%s", this.localNode, META_DATA_NODE);
        if (zkClient.checkExists().forPath(nodePath) == null) {
            zkClient.create().forPath(nodePath,
                    objectMapper.writeValueAsBytes(""));
        }
    }

    private void createLocalBrokerDataLock() throws Exception {
        String nodePath = String.format("%s/%s", this.localNode, META_DATA_LOCK);
        if (zkClient.checkExists().forPath(nodePath) == null) {
            zkClient.create().forPath(nodePath,
                    objectMapper.writeValueAsBytes(""));
        }
    }

    private void createLocalBrokerQueueNode() throws Exception {
        String nodePath = String.format("%s/%s", this.localNode, QUEUE_NODE);
        if (zkClient.checkExists().forPath(nodePath) == null) {
            zkClient.create().forPath(nodePath,
                    objectMapper.writeValueAsBytes(BrokerQueueNode.initBrokerQueueNode()));
        }
    }


    public void updateSynchronizedLocalBrokerHeartNode(String localAddress, BrokerHeartNode source, boolean isCover) {
        String nodePath = String.format("%s/%s/%s", brokersNode, localAddress, HEART_NODE);
        try {
            if (this.brokerHeartLock.acquire(30, TimeUnit.SECONDS)) {
                BrokerHeartNode target = objectMapper.readValue(zkClient.getData().forPath(nodePath), BrokerHeartNode.class);
                BrokerHeartNode.copy(source, target, isCover);
                zkClient.setData().forPath(nodePath,
                        objectMapper.writeValueAsBytes(target));
            }
        } catch (Exception e) {
            logger.error("{}:updateSynchronizedBrokerHeartNode error:{}", nodePath,
                    ExceptionUtil.getErrorMessage(e));
        } finally {
            try {
                if (this.brokerHeartLock.isAcquiredInThisProcess()) {
                    this.brokerHeartLock.release();
                }
            } catch (Exception e) {
                logger.error("{}:updateSynchronizedBrokerHeartNode error:{}", nodePath,
                        ExceptionUtil.getErrorMessage(e));
            }
        }
    }


    public void updateSynchronizedLocalQueueNode(String localAddress, BrokerQueueNode source) {
        String nodePath = String.format("%s/%s/%s", brokersNode, localAddress, QUEUE_NODE);
        try {
            if (this.brokerQueueLock.acquire(30, TimeUnit.SECONDS)) {
                zkClient.setData().forPath(nodePath, objectMapper.writeValueAsBytes(source));
            }
        } catch (Exception e) {
            logger.error("{} updateSynchronizedLocalQueueNode error:{}", nodePath,
                    ExceptionUtil.getErrorMessage(e));
        } finally {

            try {
                if (this.brokerQueueLock.isAcquiredInThisProcess()) {
                    this.brokerQueueLock.release();
                }
            } catch (Exception e) {
                logger.error("{}:updateSynchronizedLocalQueueNode error:{}", nodePath,
                        ExceptionUtil.getErrorMessage(e));
            }

        }
    }

    private void initNeedLock() {
        this.masterLock = createDistributeLock(String.format(
                "%s/%s", this.distributeRootNode, "masterLock"));

        this.brokerHeartLock = createDistributeLock(String.format(
                "%s/%s", this.distributeRootNode, "brokerheartlock"));

        this.brokerQueueLock = createDistributeLock(String.format(
                "%s/%s", this.distributeRootNode, "brokerqueuelock"));

        interProcessMutexes.add(this.masterLock);
        interProcessMutexes.add(this.brokerHeartLock);
        interProcessMutexes.add(this.brokerQueueLock);

    }

    private InterProcessMutex createDistributeLock(String nodePath) {
        return new InterProcessMutex(zkClient, nodePath);
    }

    public boolean setMaster() {
        try {
            if (this.masterLock.acquire(10, TimeUnit.SECONDS)) {
                String zkMasterAddr = isHaveMaster();
                if (!this.localAddress.equals(zkMasterAddr) || !lastMasterAddress.equals(zkMasterAddr)) {
                    BrokersNode brokersNode = BrokersNode.initBrokersNode();
                    brokersNode.setMaster(this.localAddress);
                    this.zkClient.setData().forPath(this.brokersNode,
                            objectMapper.writeValueAsBytes(brokersNode));
                    if (zkMasterAddr != null) {
                        lastMasterAddress = zkMasterAddr;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getErrorMessage(e));
        }
        return false;
    }


    public String isHaveMaster() throws Exception {
        byte[] data = this.zkClient.getData().forPath(this.brokersNode);
        String de = new String(data);
        if (data == null || de.equals("[]")
                || StringUtils.isBlank(objectMapper.readValue(data,
                BrokersNode.class).getMaster())) {
            return null;
        }
        return objectMapper.readValue(data, BrokersNode.class).getMaster();
    }

    public void createNodeIfNotExists(String node, Object obj) throws Exception {
        if (zkClient.checkExists().forPath(node) == null) {
            zkClient.create().forPath(node,
                    objectMapper.writeValueAsBytes(obj));
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
    }

    public List<String> getBrokerDataChildren(String node) {
        try {
            String nodePath = String.format("%s/%s/%s", this.brokersNode, node, META_DATA_NODE);
            return zkClient.getChildren().forPath(nodePath);
        } catch (Exception e) {
            logger.error("getBrokerDataChildren error:{}",
                    ExceptionUtil.getErrorMessage(e));
        }
        return Lists.newArrayList();
    }

    public BrokerHeartNode getBrokerHeartNode(String node) {
        try {
            String nodePath = String.format("%s/%s/%s", this.brokersNode, node, HEART_NODE);
            BrokerHeartNode nodeSign = objectMapper.readValue(zkClient.getData()
                    .forPath(nodePath), BrokerHeartNode.class);
            return nodeSign;
        } catch (Exception e) {
            logger.error("{}:getBrokerHeartNode error:{}", node,
                    ExceptionUtil.getErrorMessage(e));
        }
        return BrokerHeartNode.initNullBrokerHeartNode();
    }

    public Map<String, BrokerQueueNode> getAllBrokerQueueNode() {
        List<String> brokerList = getAliveBrokersChildren();
        Map<String, BrokerQueueNode> queueNodeMap = Maps.newHashMap();

        for (String broker : brokerList) {
            BrokerQueueNode queueNode = getBrokerQueueNode(broker);
            queueNodeMap.put(broker, queueNode);
        }

        return queueNodeMap;
    }

    private BrokerQueueNode getBrokerQueueNode(String node) {
        try {
            String nodePath = String.format("%s/%s/%s", this.brokersNode, node, QUEUE_NODE);
            BrokerQueueNode queueNode = objectMapper.readValue(zkClient.getData().forPath(nodePath), BrokerQueueNode.class);
            return queueNode;
        } catch (Exception e) {
            logger.error("{} getBrokerQueueNode error:{}", node, ExceptionUtil.getErrorMessage(e));
        }

        return BrokerQueueNode.initBrokerQueueNode();
    }

    public List<String> getBrokersChildren() {
        try {
            return zkClient.getChildren().forPath(this.brokersNode);
        } catch (Exception e) {
            logger.error("getBrokersChildren error:{}",
                    ExceptionUtil.getErrorMessage(e));
        }
        return Lists.newArrayList();
    }

    public List<String> getAliveBrokersChildren() {
        List<String> alives = Lists.newArrayList();
        try {
            List<String> brokers = zkClient.getChildren().forPath(this.brokersNode);
            for (String broker : brokers) {
                BrokerHeartNode brokerHeartNode = getBrokerHeartNode(broker);
                if (brokerHeartNode.getAlive()) {
                    alives.add(broker);
                }
            }
        } catch (Exception e) {
            logger.error("getBrokersChildren error:{}",
                    ExceptionUtil.getErrorMessage(e));
        }
        return alives;
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

    public void removeBrokerQueueNode(String address) {
        BrokerQueueNode brokerQueueNode = new BrokerQueueNode();
        this.updateSynchronizedLocalQueueNode(address, brokerQueueNode);
    }

    @Override
    public void destroy() throws Exception {
        disableBrokerHeartNode(this.localAddress, false);
        for (Listener listener : listeners) {
            try {
                listener.close();
            } catch (Exception e) {
                logger.error("{}", e);
            }
        }
    }

    public void setEnvironmentContext(EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }

    public void setMasterNode(MasterNode masterNode) {
        this.masterNode = masterNode;
    }

    public void setWorkNode(WorkNode workNode) {
        this.workNode = workNode;
    }
}