package com.dtstack.rdos.engine.execution.base.queue;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 执行引擎对应的优先级队列信息
 * Date: 2018/1/15
 * Company: www.dtstack.com
 * @author xuchao
 */

public class GroupPriorityQueue {

    /**key: groupName*/
    private Map<String, OrderLinkedBlockingQueue<JobClient>> groupPriorityQueueMap = Maps.newHashMap();

    public GroupPriorityQueue(){
    }

    public void add(JobClient jobClient) throws InterruptedException {
        OrderLinkedBlockingQueue<JobClient> queue = groupPriorityQueueMap.computeIfAbsent(jobClient.getGroupName(),
                k -> new OrderLinkedBlockingQueue<>());

        if(queue.contains(jobClient)){
            return;
        }

        queue.put(jobClient);
    }

    public Map<String, OrderLinkedBlockingQueue<JobClient>> getGroupPriorityQueueMap() {
        return groupPriorityQueueMap;
    }

    public boolean remove(String groupName, String jobId){
        OrderLinkedBlockingQueue<JobClient> queue = groupPriorityQueueMap.get(groupName);
        if (queue == null){
            return false;
        }

        return queue.remove(jobId);
    }
}
