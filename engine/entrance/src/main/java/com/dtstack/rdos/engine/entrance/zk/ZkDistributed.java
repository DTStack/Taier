package com.dtstack.rdos.engine.entrance.zk;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
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
	
	private String heartNode;
	
	private String metaDataNode;
	
	private CuratorFramework zkClient;
	
	private static ObjectMapper objectMapper = new ObjectMapper();


	
	private static ZkDistributed zkDistributed;
	
	private ZkDistributed(Map<String,Object> nodeConfig) throws Exception {
		// TODO Auto-generated constructor stub
		this.nodeConfig  = nodeConfig;
		checkDistributedConfig();
		initZk();
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
	
	
	public void createNodeIfNotExists(String node,Object obj) throws Exception {
		if (zkClient.checkExists().forPath(node) == null) {
			try {
				zkClient.create().forPath(node,
						objectMapper.writeValueAsBytes(obj));
			} catch (KeeperException.NodeExistsException e) {
				logger.warn("%s node is Exist", node);
			}
		}
	}
	
	private void checkDistributedConfig() throws Exception {
		this.zkAddress = (String)nodeConfig.get("nodeZkAddress");
		if (StringUtils.isBlank(this.zkAddress)
				|| this.zkAddress.split("/").length < 2) {
			throw new Exception("zkAddress is error");
		}
		String[] zks = this.zkAddress.split("/");
		this.zkAddress = zks[0].trim();
		this.distributeRootNode = String.format("/%s", zks[1].trim());
		this.localAddress = (String)nodeConfig.get("localAddress");
		if (StringUtils.isBlank(this.localAddress)||this.localAddress.split(":").length < 2) {
			throw new Exception("localAddress is error");
		}
		this.brokersNode = String.format("%s/brokers", this.distributeRootNode);
		this.localNode = String.format("%s/%s", this.brokersNode,this.localAddress);
		this.heartNode = String.format("%s/%s", this.localNode,"heart");
		this.metaDataNode = String.format("%s/%s", this.localNode,"data");
	}
	
	public static ZkDistributed getZkDistributed(){
		return zkDistributed;
	}

	public void release() {
		// TODO Auto-generated method stub
		
	}

}
