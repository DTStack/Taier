package com.dtstack.rdos.engine.execution.base.queue;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.constrant.ConfigConstant;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

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
        String groupName = StringUtils.isEmpty(jobClient.getGroupName()) ? ConfigConstant.DEFAULT_GROUP_NAME : jobClient.getGroupName();
        OrderLinkedBlockingQueue<JobClient> queue = groupPriorityQueueMap.computeIfAbsent(groupName,
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
        groupName = StringUtils.isEmpty(groupName) ? ConfigConstant.DEFAULT_GROUP_NAME : groupName;
        OrderLinkedBlockingQueue<JobClient> queue = groupPriorityQueueMap.get(groupName);
        return queue.remove(jobId);
    }
}
