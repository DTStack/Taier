package com.dtstack.rdos.engine.entrance.zk.data;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * TODO 节点的队列信息
 * Date: 2018/1/12
 * Company: www.dtstack.com
 * @author xuchao
 */

public class BrokerQueueNode {

    /**key: groupName value: maxPriority*/
    private Map<String, Integer> groupQueueInfo = Maps.newHashMap();

    public Map<String, Integer> getGroupQueueInfo() {
        return groupQueueInfo;
    }

    public void setGroupQueueInfo(Map<String, Integer> groupQueueInfo) {
        this.groupQueueInfo = groupQueueInfo;
    }
}
