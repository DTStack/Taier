package com.dtstack.rdos.engine.execution.base.queue;

import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 执行引擎对应的优先级队列信息
 * Date: 2018/1/15
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class GroupPriorityQueue {

    private static final int WAIT_INTERVAL = 5000;

    private String engineType;

    private Long startId = 0L;

    private Ingestion ingestion;
    /**
     * key: groupName
     */
    private Map<String, OrderLinkedBlockingQueue<JobClient>> groupPriorityQueueMap = Maps.newHashMap();

    public GroupPriorityQueue(String engineType, Ingestion ingestion) {
        this.engineType = engineType;
        this.ingestion = ingestion;
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("acquire-" + engineType + "Job"));
        scheduledService.scheduleWithFixedDelay(
                new AcquireGroupQueueJob(),
                0,
                WAIT_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public void add(JobClient jobClient) throws InterruptedException {
        OrderLinkedBlockingQueue<JobClient> queue = groupPriorityQueueMap.computeIfAbsent(jobClient.getGroupName(),
                k -> new OrderLinkedBlockingQueue<>());

        if (queue.contains(jobClient)) {
            return;
        }

        queue.put(jobClient);
    }

    public Map<String, OrderLinkedBlockingQueue<JobClient>> getGroupPriorityQueueMap() {
        return groupPriorityQueueMap;
    }

    public boolean remove(String groupName, String jobId) {
        OrderLinkedBlockingQueue<JobClient> queue = groupPriorityQueueMap.get(groupName);
        if (queue == null) {
            return false;
        }

        return queue.remove(jobId);
    }

    private class AcquireGroupQueueJob implements Runnable {

        @Override
        public void run() {
            startId = ingestion.ingestion(GroupPriorityQueue.this, startId);
        }
    }

    public interface Ingestion {

        /**
         * 匿名函数获取engineType下的任务
         *
         * @param groupPriorityQueue
         * @param startId
         * @return
         */
        Long ingestion(GroupPriorityQueue groupPriorityQueue, Long startId);
    }
}
