package com.dtstack.engine.service.zk.data;

import com.dtstack.engine.service.queue.GroupInfo;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * TODO 节点的队列信息
 * Date: 2018/1/12
 * Company: www.dtstack.com
 * @author xuchao
 */

public class BrokerQueueNode {

    /**key1: engineType, key2: groupName, value: GroupInfo*/
    private Map<String, Map<String, GroupInfo>> groupQueueInfo = Maps.newHashMap();

    public Map<String, Map<String, GroupInfo>> getGroupQueueInfo() {
        return groupQueueInfo;
    }

    public void setGroupQueueInfo(Map<String, Map<String, GroupInfo>> groupQueueInfo) {
        this.groupQueueInfo = groupQueueInfo;
    }

    public static BrokerQueueNode initBrokerQueueNode(){
        BrokerQueueNode queueNode = new BrokerQueueNode();
        return queueNode;
    }
}
