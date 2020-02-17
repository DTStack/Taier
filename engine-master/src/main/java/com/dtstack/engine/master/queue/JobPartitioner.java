package com.dtstack.engine.master.queue;

import com.dtstack.engine.common.queue.GroupInfo;
import com.dtstack.engine.master.listener.QueueListener;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/01/17
 */
@Component
public class JobPartitioner {

    @Autowired
    private QueueListener queueListener;

    /**
     * compute job number per node
     */
    public Map<String, Integer> computeBatchJobSize(Integer type, int jobSize) {
        Map<Integer, Map<String, QueueInfo>> allNodesJobQueueInfo = queueListener.getAllNodesJobQueueInfo();
        if (allNodesJobQueueInfo.isEmpty()) {
            return null;
        }
        Map<String, QueueInfo> nodesJobQueue = allNodesJobQueueInfo.get(type);
        if (nodesJobQueue == null) {
            return null;
        }
        Map<String, Integer> nodeSort = Maps.newHashMap();
        int total = jobSize;
        for (Map.Entry<String, QueueInfo> queueInfoEntry : nodesJobQueue.entrySet()) {
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

    public Map<String, Integer> computeJobCacheSize(String jobResource, int jobSize) {
        Map<String, Map<String, GroupInfo>> allNodesGroupQueueJobResources = queueListener.getAllNodesGroupQueueInfo();
        if (allNodesGroupQueueJobResources.isEmpty()) {
            return null;
        }
        Map<String, GroupInfo> nodesGroupQueue = allNodesGroupQueueJobResources.get(jobResource);
        if (nodesGroupQueue == null) {
            return null;
        }
        Map<String, Integer> nodeSort = Maps.newHashMap();
        int total = jobSize;
        for (Map.Entry<String, GroupInfo> groupInfoEntry : nodesGroupQueue.entrySet()) {
            GroupInfo groupInfo = groupInfoEntry.getValue();
            total += groupInfo.getSize();
            nodeSort.put(groupInfoEntry.getKey(), groupInfo.getSize());
        }
        int avg = total / nodeSort.size() + 1;
        for (Map.Entry<String, Integer> entry : nodeSort.entrySet()) {
            entry.setValue(avg - entry.getValue());
        }
        return nodeSort;
    }
}
