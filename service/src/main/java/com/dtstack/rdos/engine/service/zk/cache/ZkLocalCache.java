package com.dtstack.rdos.engine.service.zk.cache;

import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.service.zk.ShardConsistentHash;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
public class ZkLocalCache implements CopyOnWriteCache<String, BrokerDataNode> {

    private volatile Map<String, BrokerDataNode> core;
    private volatile Map<String, BrokerDataNode> view;

    private volatile BrokerDataNode localDataCache;
    private volatile AtomicBoolean requiresCopyOnWrite;
    private static ZkLocalCache zkLocalCache = new ZkLocalCache();

    public static ZkLocalCache getInstance() {
        return zkLocalCache;
    }

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private static ShardConsistentHash shardsCsist = ShardConsistentHash.getInstance();

    private ZkLocalCache() {
        this.requiresCopyOnWrite = new AtomicBoolean(false);
    }

    public void init() {
        core = zkDistributed.initMemTaskStatus();
        localDataCache = core.get(zkDistributed.getLocalAddress());
    }

    public void updateLocalMemTaskStatus(String zkTaskId, Integer status) {
        if (zkTaskId == null || status == null) {
            throw new UnsupportedOperationException();
        }
        copy();
        String shard = shardsCsist.get(zkTaskId);
        localDataCache.getShards().get(shard).getShardData().put(zkTaskId, status.byteValue());
    }

    public Map<String, BrokerDataNode> getLocalCache() {
        return getView();
    }

    public BrokerDataNode getBrokerData() {
        return getView().get(zkDistributed.getLocalAddress());
    }


    public String getJobLocationAddr(String zkTaskId) {
        String shard = shardsCsist.get(zkTaskId);
        for (Map.Entry<String, BrokerDataNode> entry : core.entrySet()) {
            String addr = entry.getKey();
            Map<String, BrokerDataNode.BrokerDataInner> shardMap = entry.getValue().getShards();
            if (shardMap.containsKey(shard)) {
                if (shardMap.get(shard).getShardData().containsKey(zkTaskId)) {
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
        Set<Map.Entry<String, BrokerDataNode>> entrys = core.entrySet();
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

    @Override
    public Map<String, BrokerDataNode> cloneData() {
        try {
            return new ConcurrentHashMap<String, BrokerDataNode>(core);
        } finally {
            requiresCopyOnWrite.set(true);
        }
    }

    private void copy() {
        if (requiresCopyOnWrite.compareAndSet(true, false)) {
            core = new ConcurrentHashMap<>(core);
            view = null;
        }
    }

    private Map<String, BrokerDataNode> getView() {
        if (view == null) {
            view = Collections.unmodifiableMap(core);
        }
        return view;
    }

}
