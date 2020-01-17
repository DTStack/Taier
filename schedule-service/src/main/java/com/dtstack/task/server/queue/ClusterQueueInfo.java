package com.dtstack.task.server.queue;

import com.google.common.collect.Maps;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/28
 */
public class ClusterQueueInfo {

    private static ClusterQueueInfo clusterQueueInfo = new ClusterQueueInfo();

    public static ClusterQueueInfo getInstance() {
        return clusterQueueInfo;
    }

    /**
     * type:(0正常调度，1补数据)，value:NodeQueueInfo
     */
    private volatile Map<Integer, NodeQueueInfo> infoMap = Maps.newHashMap();

    private ClusterQueueInfo() {
    }

    public boolean isEmpty() {
        return infoMap.isEmpty();
    }

    /**
     * clusterQueueInfo:  key1: address, value1:(key2:type, value2:QueueInfo)
     */
    public void updateClusterQueueInfo(Map<String, Map<Integer, QueueInfo>> clusterQueueInfo) {
        Set<Integer> types = new HashSet<>();
        clusterQueueInfo.values().forEach(typeInfo -> {
            if (typeInfo != null){
                types.addAll(typeInfo.keySet());
            }
        });

        Map<Integer, NodeQueueInfo> newInfoMap = Maps.newHashMap();
        clusterQueueInfo.forEach((address, typeInfo) -> {
            types.forEach(type -> {
                NodeQueueInfo nodeQueueInfo = newInfoMap.computeIfAbsent(type, k -> new NodeQueueInfo(type));
                nodeQueueInfo.put(address, typeInfo.getOrDefault(type, new QueueInfo()));
            });
        });
        infoMap = newInfoMap;
    }

    /**
     * compute job number per node
     */
    public Map<String, Integer> computeQueueJobSize(Integer type, int jobSize) {
        if (isEmpty()) {
            return null;
        }
        NodeQueueInfo nodeQueueInfo = infoMap.get(type);
        if (nodeQueueInfo == null) {
            return null;
        }
        Map<String, Integer> nodeSort = Maps.newHashMap();
        int total = jobSize;
        for (Map.Entry<String, QueueInfo> queueInfoEntry : nodeQueueInfo.getQueueInfoMap().entrySet()) {
            QueueInfo queueInfo = queueInfoEntry.getValue();
            total += queueInfo.getSize();
            nodeSort.put(queueInfoEntry.getKey(), queueInfo.getSize());
        }
        int avg = (total / nodeSort.size()) + 1;
        for (Map.Entry<String, Integer> entry : nodeSort.entrySet()) {
            entry.setValue(avg - entry.getValue());
        }
        return nodeSort;
    }

}
