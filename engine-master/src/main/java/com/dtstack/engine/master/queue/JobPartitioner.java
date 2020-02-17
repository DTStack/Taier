package com.dtstack.engine.master.queue;

import com.dtstack.engine.master.zookeeper.listener.QueueListener;
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
    public Map<String, Integer> computeQueueJobSize(Integer type, int jobSize) {
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

}
