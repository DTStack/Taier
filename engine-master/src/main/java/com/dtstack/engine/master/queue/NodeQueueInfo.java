package com.dtstack.engine.master.queue;

import java.util.HashMap;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/28
 */
public class NodeQueueInfo {

    private Integer type;

    /**
     * key:address
     */
    private Map<String, QueueInfo> queueInfoMap = new HashMap<>();

    public NodeQueueInfo(Integer type) {
        this.type = type;
    }

    public void put(String address, QueueInfo queueInfo) {
        queueInfoMap.put(address, queueInfo);
    }

    public Map<String, QueueInfo> getQueueInfoMap() {
        return queueInfoMap;
    }

    public Integer getType() {
        return type;
    }
}
