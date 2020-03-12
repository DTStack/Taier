package com.dtstack.engine.service.zk.cache;

import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.service.queue.GroupInfo;
import com.dtstack.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.engine.service.node.WorkNode;
import com.dtstack.engine.service.task.QueueListener;
import com.dtstack.engine.service.zk.ZkDistributed;
import com.dtstack.engine.service.zk.ZkShardManager;
import com.dtstack.engine.service.zk.data.BrokerDataNode;
import com.dtstack.engine.service.zk.data.BrokerDataShard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
public class ZkLocalCache {

    private volatile BrokerDataNode localDataCache;

    private String localAddress;
    private int distributeQueueWeight;
    private int distributeDeviation;
    private int perShardSize;
    private static ZkLocalCache zkLocalCache = new ZkLocalCache();

    public static ZkLocalCache getInstance() {
        return zkLocalCache;
    }

    private RdosEngineJobCacheDAO engineJobCacheDao = new RdosEngineJobCacheDAO();

    private final ReentrantLock lock = new ReentrantLock();

    private ZkShardManager zkShardManager = ZkShardManager.getInstance();

    private ZkLocalCache() {
    }

    public void init(ZkDistributed zkDistributed) {
        localAddress = zkDistributed.getLocalAddress();
        localDataCache = new BrokerDataNode(new ConcurrentHashMap<String,BrokerDataShard>(16));
        distributeQueueWeight = ConfigParse.getTaskDistributeQueueWeight();
        distributeDeviation = ConfigParse.getTaskDistributeDeviation();
        perShardSize = ConfigParse.getShardSize();
        zkShardManager.init(zkDistributed);
    }

    public void updateLocalMemTaskStatus(String zkTaskId, Integer status) {
        if (zkTaskId == null || status == null) {
            throw new UnsupportedOperationException();
        }
        //任务只有在提交成功后开始zk status轮询并同时checkShard一次
        if (RdosTaskStatus.SUBMITTED.getStatus().equals(status)){
            checkShard();
        }
        String shard = localDataCache.getShard(zkTaskId);
        Lock lock = zkShardManager.tryLock(shard);
        lock.lock();
        try {
            localDataCache.getShards().get(shard).put(zkTaskId, status.byteValue());
        } finally {
            lock.unlock();
        }
    }

    public BrokerDataNode getBrokerData() {
        return localDataCache;
    }


    public String getJobLocationAddr(String jobId) {
        String addr = null;
        //先查本地
        String shard = localDataCache.getShard(jobId);
        if (localDataCache.getShards().get(shard).containsKey(jobId)) {
            addr = localAddress;
        }
        //查数据库
        if (addr==null){
            RdosEngineJobCache jobCache = engineJobCacheDao.getJobById(jobId);
            if (jobCache!=null){
                addr = jobCache.getNodeAddress();
            }
        }
        return addr;
    }

    /**
     * 选择节点间（队列负载+已提交任务 加权值）+ 误差 符合要求的node，做任务分发
     */
    public String getDistributeNode(String engineType, String groupName, List<String> excludeNodes) {
        Map<String, Map<String, GroupInfo>> allNodesGroupQueueJobResources = QueueListener.getAllNodesGroupQueueInfo();
        if (allNodesGroupQueueJobResources.isEmpty()) {
            return localAddress;
        }

        String jobResource = WorkNode.getInstance().getJobResource(engineType, groupName);
        Map<String, GroupInfo> nodesGroupQueue = allNodesGroupQueueJobResources.get(jobResource);
        if (nodesGroupQueue == null || nodesGroupQueue.isEmpty()) {
            return localAddress;
        }

        GroupInfo localGroupInfo = nodesGroupQueue.get(localAddress);
        int localQueueSize = localGroupInfo == null ? 0 : localGroupInfo.getSize();

        String node = null;
        int minWeight = Integer.MAX_VALUE;
        for (Map.Entry<String, GroupInfo> groupInfoEntry : nodesGroupQueue.entrySet()) {
            String nodeAddress = groupInfoEntry.getKey();
            GroupInfo groupInfo = groupInfoEntry.getValue();

            if (excludeNodes.contains(nodeAddress)) {
                continue;
            }
            int weight = groupInfo.getSize() * distributeQueueWeight;
            if (minWeight > weight) {
                minWeight = weight;
                node = nodeAddress;
            }
        }
        int localWeight = localQueueSize * distributeQueueWeight;
        if (localWeight - minWeight <= distributeDeviation) {
            return localAddress;
        }
        return node;
    }

    /**
     * 任务状态轮询的时候注意并发删除操作，CopyOnWrite
     */
    public Map<String, BrokerDataShard> cloneShardData() {
        return new HashMap<>(localDataCache.getShards());
    }

    public void checkShard() {
        final ReentrantLock createShardLock = this.lock;
        createShardLock.lock();
        try {
            int shardSize = localDataCache.getShards().size();
            int avg = localDataCache.getDataSize() / localDataCache.getShards().size();
            if (avg >= perShardSize) {
                zkShardManager.createShardNode(shardSize);
            }
        } finally {
            createShardLock.unlock();
        }
    }

}
