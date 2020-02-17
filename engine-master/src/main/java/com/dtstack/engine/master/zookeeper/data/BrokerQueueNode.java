package com.dtstack.engine.master.zookeeper.data;

import com.dtstack.engine.master.queue.QueueInfo;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class BrokerQueueNode {

    /**
     * key: type (0正常调度 1补数据), value: QueueInfo
     */
    private Map<Integer, QueueInfo> queueInfo = Maps.newHashMap();

    public Map<Integer, QueueInfo> getQueueInfo() {
        return queueInfo;
    }

    public void setQueueInfo(Map<Integer, QueueInfo> queueInfo) {
        this.queueInfo = queueInfo;
    }

    public static BrokerQueueNode initBrokerQueueNode(){
        return new BrokerQueueNode();
    }
}
