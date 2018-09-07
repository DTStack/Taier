package com.dtstack.rdos.engine.service.zk;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.service.db.dao.RdosNodeMachineDAO;
import com.dtstack.rdos.engine.service.zk.cache.ZkLocalCache;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataShard;
import com.dtstack.rdos.engine.service.zk.data.BrokerHeartNode;
import com.dtstack.rdos.engine.service.zk.data.BrokersNode;
import com.dtstack.rdos.engine.service.zk.task.*;
import com.dtstack.rdos.engine.service.zk.data.BrokerQueueNode;
import com.dtstack.rdos.engine.execution.base.EngineDeployInfo;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.service.enums.MachineAppType;
import com.dtstack.rdos.engine.service.enums.RdosNodeMachineType;
import com.dtstack.rdos.engine.service.send.HttpSendClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import com.netflix.curator.retry.ExponentialBackoffRetry;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
/**
 *
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ZkDistributed implements Closeable{

	private static final Logger logger = LoggerFactory.getLogger(ZkDistributed.class);

	private Map<String,Object> nodeConfig;

	private String zkAddress;

	private String localAddress;

	private String distributeRootNode;

	private String brokersNode;

	private String localNode;

	private String heartNode = "heart";

	private String metaDataNode = "data";
	private String metaDataLock = "dataLock";

	private String queueNode = "queue";

	private List<Map<String, Object>> engineTypeList;

	private CuratorFramework zkClient;

    private MasterListener masterListener;
    private ZkShardListener zkShardListener;

	private static ObjectMapper objectMapper = new ObjectMapper();

	private static volatile ZkDistributed zkDistributed;

	private InterProcessMutex masterLock;

	private InterProcessMutex brokerHeartLock;

	private InterProcessMutex brokerQueueLock;

	private String masterAddrCache = "";

	private static List<InterProcessMutex> interProcessMutexs = Lists.newArrayList();

	private static ShardConsistentHash shardsCsist = ShardConsistentHash.getInstance();

	private ExecutorService executors  = new ThreadPoolExecutor(8, 8,
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

	private RdosNodeMachineDAO rdosNodeMachineDAO = new RdosNodeMachineDAO();


	private ZkDistributed(Map<String,Object> nodeConfig) throws Exception {
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

	public ZkDistributed zkRegistration() throws Exception {
		createNodeIfNotExists(this.distributeRootNode,"");
		createNodeIfNotExists(this.brokersNode, BrokersNode.initBrokersNode());
		createNodeIfNotExists(this.localNode,"");
		initNeedLock();
		createLocalBrokerHeartNode();
		createLocalBrokerDataNode();
		createLocalBrokerDataLock();
		createLocalBrokerQueueNode();
		ZkLocalCache.getInstance().init();
		registrationDB();
		initScheduledExecutorService();
		logger.warn("init zk server success...");
		return this;
	}

	private void initScheduledExecutorService() {
        masterListener = new MasterListener();
		zkShardListener = new ZkShardListener();
		executors.execute(new HeartBeat());
		executors.execute(masterListener);
		executors.execute(new HeartBeatListener(masterListener));
		executors.execute(new TaskListener());
		executors.execute(new TaskStatusListener());
		executors.execute(new QueueListener());
		if(ConfigParse.getPluginStoreInfo()!=null){
			executors.execute(new LogStoreListener(masterListener));
		}
	}

	public boolean localIsMaster(){
        return masterListener.isMaster();
    }

	private void registrationDB() throws IOException {

		EngineDeployInfo deployInfo = new EngineDeployInfo(engineTypeList);
		String deployInfoStr = PublicUtil.objToString(deployInfo.getDeployMap());

		rdosNodeMachineDAO.insert(this.localAddress, RdosNodeMachineType.SLAVE.getType(),MachineAppType.ENGINE, deployInfoStr);
	}

	private void createLocalBrokerHeartNode() throws Exception{
		String node = String.format("%s/%s", this.localNode,heartNode);
		if (zkClient.checkExists().forPath(node) == null) {
			zkClient.create().forPath(node,
					objectMapper.writeValueAsBytes(BrokerHeartNode.initBrokerHeartNode()));
		}else{
			updateSynchronizedLocalBrokerHeartNode(this.localAddress,BrokerHeartNode.initBrokerHeartNode(),true);
		}
	}
	private void createLocalBrokerDataNode() throws Exception{
		String nodePath = String.format("%s/%s", this.localNode,metaDataNode);
		if (zkClient.checkExists().forPath(nodePath) == null) {
			zkClient.create().forPath(nodePath,
					objectMapper.writeValueAsBytes(""));
		}
	}
	private void createLocalBrokerDataLock() throws Exception{
		String nodePath = String.format("%s/%s", this.localNode,metaDataLock);
		if (zkClient.checkExists().forPath(nodePath) == null) {
			zkClient.create().forPath(nodePath,
					objectMapper.writeValueAsBytes(""));
		}
	}

    private void createLocalBrokerQueueNode() throws Exception{
        String nodePath = String.format("%s/%s", this.localNode, queueNode);
        if (zkClient.checkExists().forPath(nodePath) == null) {
            zkClient.create().forPath(nodePath,
                    objectMapper.writeValueAsBytes(BrokerQueueNode.initBrokerQueueNode()));
        }
    }


    public void updateSynchronizedLocalBrokerHeartNode(String localAddress,BrokerHeartNode source,boolean isCover){
		String nodePath = String.format("%s/%s/%s", brokersNode,localAddress,heartNode);
		try {
			if(this.brokerHeartLock.acquire(30, TimeUnit.SECONDS)){
				BrokerHeartNode target = objectMapper.readValue(zkClient.getData().forPath(nodePath), BrokerHeartNode.class);
				BrokerHeartNode.copy(source, target,isCover);
				zkClient.setData().forPath(nodePath,
						objectMapper.writeValueAsBytes(target));
			}
		} catch (Exception e) {
			logger.error("{}:updateSynchronizedBrokerHeartNode error:{}", nodePath,
					ExceptionUtil.getErrorMessage(e));
		} finally{
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


	public void updateSynchronizedLocalQueueNode(String localAddress, BrokerQueueNode source){
        String nodePath = String.format("%s/%s/%s", brokersNode, localAddress, queueNode);
        try{
            if(this.brokerQueueLock.acquire(30, TimeUnit.SECONDS)){
                zkClient.setData().forPath(nodePath, objectMapper.writeValueAsBytes(source));
            }
        }catch (Exception e){
            logger.error("{} updateSynchronizedLocalQueueNode error:{}", nodePath,
                    ExceptionUtil.getErrorMessage(e));
        }finally {

            try{
                if(this.brokerQueueLock.isAcquiredInThisProcess()){
                    this.brokerQueueLock.release();
                }
            } catch (Exception e) {
                logger.error("{}:updateSynchronizedLocalQueueNode error:{}", nodePath,
                        ExceptionUtil.getErrorMessage(e));
            }

        }
    }

	public void synchronizedBrokerDataShard(String nodeAddress, String shard, BrokerDataShard brokerDataShard,boolean isCover){
		String nodePath = String.format("%s/%s/%s/%s",this.brokersNode,nodeAddress,metaDataNode,shard);
		InterProcessMutex lock = zkShardListener.getShardLock(shard);
		try {
			if(lock!=null&&lock.acquire(30, TimeUnit.SECONDS)){
				if (!isCover) {
					BrokerDataShard target = objectMapper.readValue(zkClient.getData().forPath(nodePath), BrokerDataShard.class);
					target.getMetas().putAll(brokerDataShard.getMetas());
					brokerDataShard = target;
				}
				zkClient.setData().forPath(nodePath, objectMapper.writeValueAsBytes(brokerDataShard));
			}
		} catch (Exception e) {
			logger.error("{}:updateSynchronizedBrokerDatalock error:{}", nodePath,
					ExceptionUtil.getErrorMessage(e));
		} finally{
			try {
				if (lock!=null&&lock.isAcquiredInThisProcess()) {
					lock.release();
				}
			} catch (Exception e) {
				logger.error("{}:updateSynchronizedBrokerDatalock error:{}", nodePath,
						ExceptionUtil.getErrorMessage(e));
			}
		}
	}

	private void initNeedLock(){
		this.masterLock = createDistributeLock(String.format(
				"%s/%s", this.distributeRootNode, "masterLock"));

		this.brokerHeartLock = createDistributeLock(String.format(
				"%s/%s", this.distributeRootNode, "brokerheartlock"));

		this.brokerQueueLock = createDistributeLock(String.format(
                "%s/%s", this.distributeRootNode, "brokerqueuelock"));

		interProcessMutexs.add(this.masterLock);
		interProcessMutexs.add(this.brokerHeartLock);
        interProcessMutexs.add(this.brokerQueueLock);

	}

	private InterProcessMutex createDistributeLock(String nodePath){
		return new InterProcessMutex(zkClient,nodePath);
	}

	public boolean setMaster() {
		try {
			if(this.masterLock.acquire(10, TimeUnit.SECONDS)){
                String zkMasterAddr = isHaveMaster();
				if(!this.localAddress.equals(zkMasterAddr) || !masterAddrCache.equals(zkMasterAddr)){
					BrokersNode brokersNode = BrokersNode.initBrokersNode();
					brokersNode.setMaster(this.localAddress);
					this.zkClient.setData().forPath(this.brokersNode,
							objectMapper.writeValueAsBytes(brokersNode));
					rdosNodeMachineDAO.updateOneTypeMachineToSlave(MachineAppType.ENGINE.getType());
					rdosNodeMachineDAO.updateMachineType(this.localAddress,RdosNodeMachineType.MASTER.getType());
					if(zkMasterAddr != null){
						masterAddrCache = zkMasterAddr;
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
		if (data == null||de.equals("[]")
				|| StringUtils.isBlank(objectMapper.readValue(data,
				BrokersNode.class).getMaster())) {
			return null;
		}
		return objectMapper.readValue(data, BrokersNode.class).getMaster();
	}

	public Map<String, BrokerDataNode> initMemTaskStatus(){
		Map<String, BrokerDataNode> memTaskStatus = Maps.newConcurrentMap();
		List<String> brokers = getBrokersChildren();
		for(String broker:brokers){
			BrokerHeartNode brokerHeartNode = getBrokerHeartNode(broker);
			if(brokerHeartNode.getAlive()){
				Map<String,BrokerDataShard> brokerDataShardMap = this.getBrokerDataNode(broker);
				BrokerDataNode brokerDataNode = memTaskStatus.computeIfAbsent(broker, k-> new BrokerDataNode(brokerDataShardMap));
			}
		}
		return memTaskStatus;
	}

	public void createNodeIfNotExists(String node, Object obj) throws Exception{
		if (zkClient.checkExists().forPath(node) == null) {
			zkClient.create().forPath(node,
					objectMapper.writeValueAsBytes(obj));
		}
	}

	private void checkDistributedConfig() throws Exception {
		this.zkAddress = ConfigParse.getNodeZkAddress();
		if (StringUtils.isBlank(this.zkAddress)
				|| this.zkAddress.split("/").length < 2) {
			throw new RdosException("zkAddress is error");
		}
		String[] zks = this.zkAddress.split("/");
		this.zkAddress = zks[0].trim();
		this.distributeRootNode = String.format("/%s", zks[1].trim());
		this.localAddress = ConfigParse.getLocalAddress();
		if (StringUtils.isBlank(this.localAddress)||this.localAddress.split(":").length < 2) {
			throw new RdosException("localAddress is error");
		}
		this.brokersNode = String.format("%s/brokers", this.distributeRootNode);
		this.localNode = String.format("%s/%s", this.brokersNode,this.localAddress);
		this.engineTypeList = ConfigParse.getEngineTypeList();
	}

	public Map<String,BrokerDataShard> getBrokerDataNode(String node) {
		try {
			List<String> shards = getBrokerDataChildren(node);
			Map<String,BrokerDataShard> shardMap = new HashMap<>(shards.size());
			for (String shard:shards){
				BrokerDataShard shardNode = getBrokerDataShard(node,shard);
				shardMap.put(shard,shardNode);
			}
			return shardMap;
		} catch (Exception e) {
			logger.error("{}:getBrokerNodeData error:{}", node,
					ExceptionUtil.getErrorMessage(e));
		}
		return null;
	}

	public List<String> getBrokerDataChildren(String node) {
		try {
			String nodePath = String.format("%s/%s/%s", this.brokersNode, node,this.metaDataNode);
			return zkClient.getChildren().forPath(nodePath);
		} catch (Exception e) {
			logger.error("getBrokerDataChildren error:{}",
					ExceptionUtil.getErrorMessage(e));
		}
		return Lists.newArrayList();
	}

	public BrokerDataShard getBrokerDataShard(String node, String shard) {
		try {
			String nodePath = String.format("%s/%s/%s/%s", this.brokersNode, node,this.metaDataNode,shard);
			BrokerDataShard nodeSign = objectMapper.readValue(zkClient.getData()
					.forPath(nodePath), BrokerDataShard.class);
			return nodeSign;
		} catch (Exception e) {
			logger.error("{}/{}/{}:getBrokerDataShard error:{}", this.localNode, this.metaDataNode, shard, ExceptionUtil.getErrorMessage(e));
		}
		return null;
	}

	public boolean createBrokerDataShard(String shard) {
		try {
			String nodePath = String.format("%s/%s/%s", this.localNode,this.metaDataNode,shard);
			if (zkClient.checkExists().forPath(nodePath) == null) {
				zkClient.create().forPath(nodePath,
						objectMapper.writeValueAsBytes(BrokerDataShard.initBrokerDataShard()));
				return true;
			}
		} catch (Exception e) {
			logger.error("{}/{}/{}:createBrokerDataShard error:{}", this.localNode, this.metaDataNode, shard, ExceptionUtil.getErrorMessage(e));
		}
		return false;
	}

	public InterProcessMutex createBrokerDataShardLock(String shardLock) {
			String nodePath = String.format("%s/%s/%s", this.localNode,this.metaDataLock,shardLock);
		return new InterProcessMutex(zkClient,nodePath);
	}

	public void deleteBrokerDataShard(String shard) {
		String nodePath = String.format("%s/%s/%s", this.localNode,this.metaDataNode,shard);
		try {
			zkClient.delete().forPath(nodePath);
		} catch (Exception e) {
			logger.error("{}:deleteBrokerDataShard error:{}", nodePath, ExceptionUtil.getErrorMessage(e));
		}
	}
	public void deleteBrokerDataShardLock(String shard) {
		String nodePath = String.format("%s/%s/%s", this.localNode,this.metaDataLock,shard);
		try {
			zkClient.delete().forPath(nodePath);
		} catch (Exception e) {
			logger.error("{}:deleteBrokerDataShard error:{}", nodePath, ExceptionUtil.getErrorMessage(e));
		}
	}

	public BrokerHeartNode getBrokerHeartNode(String node) {
		try {
			String nodePath = String.format("%s/%s/%s", this.brokersNode, node,this.heartNode);
			BrokerHeartNode nodeSign = objectMapper.readValue(zkClient.getData()
					.forPath(nodePath), BrokerHeartNode.class);
			return nodeSign;
		} catch (Exception e) {
			logger.error("{}:getBrokerHeartNode error:{}", node,
					ExceptionUtil.getErrorMessage(e));
		}
		return null;
	}

	public Map<String, BrokerQueueNode> getAllBrokerQueueNode(){
        List<String> brokerList = getAliveBrokersChildren();
        Map<String, BrokerQueueNode> queueNodeMap = Maps.newHashMap();

        for(String broker : brokerList){
            BrokerQueueNode queueNode = getBrokerQueueNode(broker);
            queueNodeMap.put(broker, queueNode);
        }

        return queueNodeMap;
    }

    public BrokerQueueNode getBrokerQueueNode(String node){
	    try{
            String nodePath = String.format("%s/%s/%s", this.brokersNode, node, this.queueNode);
            BrokerQueueNode queueNode = objectMapper.readValue(zkClient.getData().forPath(nodePath), BrokerQueueNode.class);
            return queueNode;
        }catch (Exception e){
            logger.error("{} getBrokerQueueNode error:{}", node, ExceptionUtil.getErrorMessage(e));
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
		return Lists.newArrayList();
	}

	public List<String> getAliveBrokersChildren() {
		List<String> alives = Lists.newArrayList();
		try {
			List<String> brokers = zkClient.getChildren().forPath(this.brokersNode);
			for(String broker:brokers) {
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


	public static ZkDistributed getZkDistributed(){
		return zkDistributed;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	private void lockRelease(){
		interProcessMutexs.forEach(lock->{
			try{
				if(lock.isAcquiredInThisProcess()){
					lock.release();
				}
			}catch (Exception e){
				logger.error("",e);
			}
		});
		zkShardListener.lockRelease();
	}

	public void disableBrokerHeartNode(String localAddress){
		BrokerHeartNode disableBrokerHeartNode = BrokerHeartNode.initNullBrokerHeartNode();
		zkDistributed.updateSynchronizedLocalBrokerHeartNode(localAddress,disableBrokerHeartNode, false);
		this.rdosNodeMachineDAO.disableMachineNode(localAddress, RdosNodeMachineType.SLAVE.getType());
	}

	public void removeBrokerQueueNode(String address){
		BrokerQueueNode brokerQueueNode = new BrokerQueueNode();
		zkDistributed.updateSynchronizedLocalQueueNode(address, brokerQueueNode);
	}

	public void dataMigration(String nodeAddress) {
        List<InterProcessMutex> mutexes = null;
        try {
        	//所得所有的锁
            mutexes = this.acquireBrokerLock(Lists.newArrayList(nodeAddress));
			if(mutexes!=null){
				BrokerHeartNode bNode = this.getBrokerHeartNode(nodeAddress);
				if(!bNode.getAlive()){
					BrokerDataNode dataNode = getDataNode(nodeAddress);
					int total = dataNode.getDataSize();
					if (total <=0){
						return;
					}
					//获取当前可用节点任务量，计算所有节点任务综合
					List<String> brokers = getBrokersChildren();
                    Map<String,Integer> otherDataSize = Maps.newHashMap();
					Map<String,BrokerDataNode> migrateDatas = Maps.newHashMap();
					for(String broker:brokers){
						BrokerHeartNode brokerHeartNode = getBrokerHeartNode(broker);
						if(brokerHeartNode.getAlive()){
							BrokerDataNode bbs = getDataNode(broker);
                            int dataSize = bbs.getDataSize();
                            otherDataSize.put(broker, dataSize);
							bbs.clear();
							migrateDatas.put(broker,bbs);
							total = dataSize + total;
						}
					}
                    //有可用的迁移节点
					if(migrateDatas.size()>0){
					    //每个节点需要接收的任务量，+1 除余后的任务
						int a = total/migrateDatas.size()+1;
                        A:for(Map.Entry<String,BrokerDataNode> other:migrateDatas.entrySet()){
							int index = 0;
							int c = otherDataSize.get(other.getKey());
							if(c < a){
								B:for(Map.Entry<String,BrokerDataShard> shard:dataNode.getShards().entrySet()){
									Iterator<Map.Entry<String,Byte>> shardIt = shard.getValue().getMetas().entrySet().iterator();
									C:while (shardIt.hasNext()){
                                        index++;
                                        if(index <= a-c){
                                            Map.Entry<String,Byte> shardData = shardIt.next();
											String key  = TaskIdUtil.convertToMigrationJob(shardData.getKey());
                                            other.getValue().putElement(key, shardData.getValue());
											shardIt.remove();
                                            continue C;
                                        }
                                        continue A;
                                    }
								}
							}
						}
						for(Map.Entry<String,BrokerDataNode> entry:migrateDatas.entrySet()){
							for (Map.Entry<String, BrokerDataShard> shard:entry.getValue().getShards().entrySet()){
								Map<String,Object> params = new HashMap<>(2);
								params.put("shardName",shard.getKey());
								params.put("shardValue",shard.getValue());
								HttpSendClient.migrationShard(entry.getKey(),params);
							}
						}
						for (String shard:dataNode.getShards().keySet()){
							this.synchronizedBrokerDataShard(nodeAddress, shard, BrokerDataShard.initBrokerDataShard(), true);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("dataMigration fail:{}",ExceptionUtil.getErrorMessage(e));
		}finally{
			releaseLock(mutexes);
		}
	}

	public List<InterProcessMutex> acquireBrokerLock(List<String> brokers){
		if (CollectionUtils.isEmpty(brokers)){
			return null;
		}
		List<InterProcessMutex> allLocks = new ArrayList<>();
		boolean lock = true;
        try {
			for(String broker:brokers) {
				List<String> shards = getBrokerDataChildren(broker);
				//排个顺序，避免由此产生的顺序死锁，提高线程活性
				Collections.sort(shards);
				for (String shard:shards){
					String nodePath = String.format("%s/%s/%s/%s", brokersNode, broker, this.metaDataLock,shard+"_lock");
					allLocks.add(new InterProcessMutex(zkClient,nodePath));
				}
			}
			for (InterProcessMutex mutex:allLocks){
				if (!mutex.acquire(30, TimeUnit.SECONDS)){
                    lock = false;
                    logger.error("acquireBrokerLock fail, acquire time out");
                    break;
				}
			}
		} catch (Exception e) {
            logger.error("acquireBrokerLock error:{}",
					ExceptionUtil.getErrorMessage(e));
            lock = false;
        }
        if (lock==false){
			releaseLock(allLocks);
            allLocks = null;
        }
        return allLocks;
    }

    public void releaseLock(List<InterProcessMutex> allLocks){
		if (CollectionUtils.isEmpty(allLocks)){
			return;
		}
		for (InterProcessMutex mutex:allLocks){
			try {
				if(mutex.isAcquiredInThisProcess()){
					mutex.release();
				}
			} catch (Exception e) {
				logger.error("releaseLock release lock fail:{}",ExceptionUtil.getErrorMessage(e));
			}
		}
	}


	private BrokerDataNode getDataNode(String nodeAddress){
		Map<String,BrokerDataShard> brokerDataShardMap = this.getBrokerDataNode(nodeAddress);
		BrokerDataNode brokerDataNode = new BrokerDataNode(brokerDataShardMap);
		return brokerDataNode;
	}

	@Override
	public void close() throws IOException {
		try{
			disableBrokerHeartNode(this.localAddress);
			lockRelease();
			List<String> nodes = getAliveBrokersChildren();
			if(nodes.size() > 0){
				HttpSendClient.migration(this.localAddress,nodes.get(0));
			}
			executors.shutdown();
		}catch (Throwable e){
			logger.error("",e);
		}
	}
}