package com.dtstack.engine.common.queue;

import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 执行引擎对应的优先级队列信息
 * Date: 2018/1/15
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class GroupPriorityQueue {

    private static final int WAIT_INTERVAL = 5000;
    private static final int QUEUE_SIZE_LIMITED = ConfigParse.getQueueSize();
    private static final int STOP_ACQUIRE_LIMITED = 10;

    /**
     * queue 初始为不进行调度，但队列的负载超过 QUEUE_SIZE_LIMITED 的阈值时触发调度
     */
    private AtomicBoolean running = new AtomicBoolean(false);

    private AtomicLong startId = new AtomicLong(0);
    private AtomicInteger stopAcquireCount = new AtomicInteger(0);

    private Ingestion ingestion;
    /**
     * key: groupName
     */
    private Map<String, OrderLinkedBlockingQueue<JobClient>> groupPriorityQueueMap = Maps.newHashMap();

    /**
     * 每个GroupPriorityQueue中增加独立线程，以定时调度方式从数据库中获取任务。（数据库查询以id和优先级为条件）
     *
     * @param engineType
     * @param ingestion
     */
    public GroupPriorityQueue(String engineType, Ingestion ingestion) {
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

        if (queue.remove(jobId)) {
            return true;
        }
        return false;
    }

    /**
     * 如果当前队列没有开启调度并且队列的大小小于100，则直接提交到队列之中
     * 否则，只在保存到jobCache表, 并且判断调度是否停止，如果停止则开启调度。
     *
     * @return
     */
    public boolean isBlocked() {
        boolean blocked = running.get() || queueSize() >= QUEUE_SIZE_LIMITED;
        if (blocked && !running.get()) {
            running.set(true);
            stopAcquireCount.set(0);
        }
        return blocked;
    }

    private long queueSize() {
        return groupPriorityQueueMap.values().stream().mapToInt(OrderLinkedBlockingQueue::size).count();
    }

    public void resetStartId() {
        startId.set(0);
    }

    private class AcquireGroupQueueJob implements Runnable {

        @Override
        public void run() {

            if (Boolean.FALSE == running.get() && queueSize() >= QUEUE_SIZE_LIMITED) {
                return;
            }

            /**
             * 如果队列中的任务数量小于 ${GroupPriorityQueue.QUEUE_SIZE_LIMITED} , 在连续调度了  ${GroupPriorityQueue.STOP_ACQUIRE_LIMITED} 次都没有查询到新的数据，则停止调度
             */
            long limitId = ingestion.ingestion(GroupPriorityQueue.this, startId.get(), QUEUE_SIZE_LIMITED);
            if (limitId != startId.get()){
                stopAcquireCount.set(0);
            } else if (stopAcquireCount.incrementAndGet() >= STOP_ACQUIRE_LIMITED) {
                running.set(false);
            }
            startId.set(limitId);
        }
    }

    public interface Ingestion {

        /**
         * 匿名函数获取engineType下的任务
         *
         * @param groupPriorityQueue
         * @param startId
         * @param limited
         * @return
         */
        Long ingestion(GroupPriorityQueue groupPriorityQueue, long startId, int limited);
    }
}
