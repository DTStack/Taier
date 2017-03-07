package com.dtstack.rdos.engine.entrance.zk;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerHeartNode;
import com.dtstack.rdos.engine.entrance.zk.data.BrokersNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import com.netflix.curator.retry.ExponentialBackoffRetry;


/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ZkDistributed {
	
	private static final Logger logger = LoggerFactory.getLogger(ZkDistributed.class);

	private Map<String,Object> nodeConfig;
	
	private String zkAddress;
	
	private String localAddress;

	private String distributeRootNode;

	private String brokersNode;

	private String localNode;
	
	private String heartNode = "heart";
	
	private String metaDataNode = "data";
	
	private CuratorFramework zkClient;
	
	private static ObjectMapper objectMapper = new ObjectMapper();

	private static ZkDistributed zkDistributed;
	
	private Map<String,BrokerDataNode> memTaskStatus = Maps.newHashMap();
	
	private InterProcessMutex masterlock;
	
	private InterProcessMutex brokerDataLock;

	private InterProcessMutex brokerHeartLock;

	private ExecutorService executors  = Executors.newFixedThreadPool(5);

	
	private ZkDistributed(Map<String,Object> nodeConfig) throws Exception {
		// TODO Auto-generated constructor stub
		this.nodeConfig  = nodeConfig;
		checkDistributedConfig();
		initZk();
		zkRegistration();
	}

	public static ZkDistributed createZkDistributed(Map<String,Object> nodeConfig) throws Exception{
		if(zkDistributed == null){
			synchronized(ZkDistributed.class){
				if(zkDistributed == null){
					zkDistributed = new ZkDistributed(nodeConfig);
				}
			}
		}
		return zkDistributed;
	}
	
	private void initZk() throws IOException {
		this.zkClient = CuratorFrameworkFactory.builder()
		.connectString(this.zkAddress).retryPolicy(new ExponentialBackoffRetry(1000, 3))
		.connectionTimeoutMs(1000)
		.sessionTimeoutMs(1000).build();
		this.zkClient.start();
		logger.warn("connector zk success...");
	}
	
	private void zkRegistration() throws Exception {
		createNodeIfExists(this.distributeRootNode,"");
		createNodeIfExists(this.brokersNode,BrokersNode.initBrokersNode());
		createNodeIfExists(this.localNode,"");
		initNeedLock();
		createLocalBrokerHeartNode();
		createLocalBrokerDataNode();
		initMemTaskStatus();
		setMaster();
		initScheduledExecutorService();
	}
	
	private void initScheduledExecutorService() {
	
	}
	
	
	private void createLocalBrokerHeartNode() throws Exception{
		String node = String.format("%s/%s", this.localNode,heartNode);
		if (zkClient.checkExists().forPath(node) == null) {
			zkClient.create().forPath(node,
					objectMapper.writeValueAsBytes(BrokerHeartNode.initBrokerHeartNode()));
		}else{
			updateSynchronizedLocalBrokerHeartNode(BrokerHeartNode.initBrokerHeartNode());
		}
	}
	private void createLocalBrokerDataNode() throws Exception{
		String nodePath = String.format("%s/%s", this.localNode,metaDataNode);
		if (zkClient.checkExists().forPath(nodePath) == null) {
			zkClient.create().forPath(nodePath,
					objectMapper.writeValueAsBytes(BrokerDataNode.initBrokerDataNode()));
		}else{
			updateSynchronizedLocalBrokerDatalock(BrokerDataNode.initBrokerDataNode());
		}
	}
	
	public void updateSynchronizedLocalBrokerHeartNode(BrokerHeartNode brokerHeartNode){
		String nodePath = String.format("%s/%s", this.localNode,heartNode);
		try {
			this.brokerHeartLock.acquire(30, TimeUnit.SECONDS);
			zkClient.setData().forPath(nodePath,
					objectMapper.writeValueAsBytes(brokerHeartNode));
		} catch (Exception e) {
			logger.error("{}:updateSynchronizedBrokerHeartNode error:{}", nodePath,
					ExceptionUtil.getErrorMessage(e));
		} finally{
			try {
				if (this.brokerHeartLock.isAcquiredInThisProcess()) this.brokerHeartLock.release();
			} catch (Exception e) {
				logger.error("{}:updateSynchronizedBrokerHeartNode error:{}", nodePath,
						ExceptionUtil.getErrorMessage(e));
			}
		}
	}
		
	public void updateSynchronizedLocalBrokerDatalock(BrokerDataNode brokerDataNode){
		String nodePath = String.format("%s/%s", this.localNode,metaDataNode);
		try {
			this.brokerDataLock.acquire(30, TimeUnit.SECONDS);
			zkClient.setData().forPath(nodePath,
					objectMapper.writeValueAsBytes(brokerDataNode));
		} catch (Exception e) {
			logger.error("{}:updateSynchronizedBrokerDatalock error:{}", nodePath,
					ExceptionUtil.getErrorMessage(e));
		} finally{
			try {
				if (this.brokerDataLock.isAcquiredInThisProcess()) this.brokerDataLock.release();
			} catch (Exception e) {
				logger.error("{}:updateSynchronizedBrokerDatalock error:{}", nodePath,
						ExceptionUtil.getErrorMessage(e));
			}
		}
	}
	
	private void initNeedLock(){
		this.masterlock = createDistributeLock(String.format(
				"%s/%s", this.distributeRootNode, "masterlock"));
		
		this.brokerDataLock = createDistributeLock(String.format(
				"%s/%s", this.distributeRootNode, "brokerdatalock"));
		
		this.brokerHeartLock = createDistributeLock(String.format(
				"%s/%s", this.distributeRootNode, "brokerheartlock"));
	}
	
	private InterProcessMutex createDistributeLock(String nodePath){
		return new InterProcessMutex(zkClient,nodePath);
	}
	
	public boolean setMaster() {
		boolean flag = false;
		try {
			String master = isHaveMaster();
			if (this.localAddress.equals(master))return true;
			boolean isMaster = this.masterlock.acquire(10, TimeUnit.SECONDS);
			if(isMaster){
				BrokersNode brokersNode = BrokersNode.initBrokersNode();
				brokersNode.setMaster(this.localAddress);
				this.zkClient.setData().forPath(this.brokersNode,
						objectMapper.writeValueAsBytes(brokersNode));
				flag = true;
			}
		} catch (Exception e) {
			logger.error(ExceptionUtil.getErrorMessage(e));
		}
		return flag;
	}
	
	public String isHaveMaster() throws Exception {
		byte[] data = this.zkClient.getData().forPath(this.brokersNode);
		if (data == null
				|| StringUtils.isBlank(objectMapper.readValue(data,
						BrokersNode.class).getMaster())) {
			return null;
		}
		return objectMapper.readValue(data, BrokersNode.class).getMaster();
	}
	
	public synchronized void initMemTaskStatus(){
		List<String> brokers = getBrokersChildren();
		for(String broker:brokers){
			memTaskStatus.put(broker, getBrokerDataNode(broker));
		}
	}
	
	public void createNodeIfExists(String node,Object obj) throws Exception{
			if (zkClient.checkExists().forPath(node) == null) {
				zkClient.create().forPath(node,
						objectMapper.writeValueAsBytes(obj));
			}else{
				zkClient.setData().forPath(node, objectMapper.writeValueAsBytes(obj));
			}
	}
	
	private void checkDistributedConfig() throws Exception {
		this.zkAddress = (String)nodeConfig.get("nodeZkAddress");
		if (StringUtils.isBlank(this.zkAddress)
				|| this.zkAddress.split("/").length < 2) {
			throw new RdosException("zkAddress is error");
		}
		String[] zks = this.zkAddress.split("/");
		this.zkAddress = zks[0].trim();
		this.distributeRootNode = String.format("/%s", zks[1].trim());
		this.localAddress = (String)nodeConfig.get("localAddress");
		if (StringUtils.isBlank(this.localAddress)||this.localAddress.split(":").length < 2) {
			throw new RdosException("localAddress is error");
		}
		this.brokersNode = String.format("%s/brokers", this.distributeRootNode);
		this.localNode = String.format("%s/%s", this.brokersNode,this.localAddress);
	}
	
	public BrokerDataNode getBrokerDataNode(String node) {
		try {
			String nodePath = String.format("%s/%s/%s", this.brokersNode, node,this.metaDataNode);
			BrokerDataNode nodeSign = objectMapper.readValue(zkClient.getData()
					.forPath(nodePath), BrokerDataNode.class);
			return nodeSign;
		} catch (Exception e) {
			logger.error("{}:getBrokerNodeData error:{}", node,
					ExceptionUtil.getErrorMessage(e));
		}
		return null;
	}
	
	public List<String> getBrokersChildren() {
		try {
			return zkClient.getChildren().forPath(this.brokersNode);
		} catch (Exception e) {
			logger.error("getBrokersChildren error:{}",
					ExceptionUtil.getErrorMessage(e));
		}
		return null;
	}

	public static ZkDistributed getZkDistributed(){
		return zkDistributed;
	}
	
	public void release() {
		// TODO Auto-generated method stub
	}

}
