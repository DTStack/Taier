package com.dtstack.rdos.engine.service.zk.cache;

import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.service.zk.ShardConsistentHash;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
public class ZkLocalCache {

    private Map<String, BrokerDataNode> zkCache;

    private BrokerDataNode localDataCache;

    private static ZkLocalCache zkLocalCache = new ZkLocalCache();

    public static ZkLocalCache getInstance() {
        return zkLocalCache;
    }

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private static ShardConsistentHash shardsCsist = ShardConsistentHash.getInstance();

    private ZkLocalCache() {
    }

    public void init() {
        zkCache = zkDistributed.initMemTaskStatus();
        localDataCache = zkCache.get(zkDistributed.getLocalAddress());
    }

    public void updateLocalMemTaskStatus(String zkTaskId, Integer status) {
        if (zkTaskId == null || status == null) {
            throw new UnsupportedOperationException();
        }
        String shard = shardsCsist.get(zkTaskId);
        localDataCache.getShards().get(shard).getShardData().put(zkTaskId, status.byteValue());
    }

    public Map<String, BrokerDataNode> getLocalCache() {
        return zkCache;
    }

    public BrokerDataNode getBrokerData() {
        return zkCache.get(zkDistributed.getLocalAddress());
    }


    public String getJobLocationAddr(String zkTaskId){
        String shard = shardsCsist.get(zkTaskId);
        for(Map.Entry<String, BrokerDataNode> entry : zkCache.entrySet()){
            String addr = entry.getKey();
            Map<String,BrokerDataNode.BrokerDataInner> shardMap = entry.getValue().getShards();
            if (shardMap.containsKey(shard)){
                if (shardMap.get(shard).getShardData().containsKey(zkTaskId)){
                    return addr;
                }
            }
        }
        return null;
    }

    /**
     * 选择节点间队列负载最小的node，做任务分发
     */
    public String getDistributeNode(List<String> excludeNodes) {
        int def = Integer.MAX_VALUE;
        String node = null;
        Set<Map.Entry<String, BrokerDataNode>> entrys = zkCache.entrySet();
        for (Map.Entry<String, BrokerDataNode> entry : entrys) {
            String targetNode = entry.getKey();
            if (excludeNodes.contains(targetNode)) {
                continue;
            }
            int size = 0;
            for (Map.Entry<String, BrokerDataNode.BrokerDataInner> shardEntry : entry.getValue().getShards().entrySet()) {
                size += getDistributeJobCount(shardEntry.getValue());
            }
            if (size < def) {
                def = size;
                node = targetNode;
            }
        }
        return node;
    }

    private int getDistributeJobCount(BrokerDataNode.BrokerDataInner brokerDataInner) {
        int count = 0;
        for (byte status : brokerDataInner.getShardData().values()) {
            if (status == RdosTaskStatus.RESTARTING.getStatus()
                    || status == RdosTaskStatus.WAITCOMPUTE.getStatus()
                    || status == RdosTaskStatus.WAITENGINE.getStatus()) {
                count++;
            }
        }
        return count;
    }
}
