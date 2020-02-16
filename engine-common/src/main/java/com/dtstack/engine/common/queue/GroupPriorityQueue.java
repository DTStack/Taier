package com.dtstack.engine.common.queue;

import com.dtstack.engine.common.JobSubmitDealer;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/16
 */
public class GroupPriorityQueue {

    private static final int WAIT_INTERVAL = 5000;
    private static final int STOP_ACQUIRE_LIMITED = 10;
    /**
     * queue 初始为不进行调度，但队列的负载超过 queueSizeLimited 的阈值时触发调度
     */
    private AtomicBoolean running = new AtomicBoolean(false);

    private AtomicLong startId = new AtomicLong(0);

    private AtomicInteger stopAcquireCount = new AtomicInteger(0);

    private String jobResource;
    private int queueSizeLimited;
    private long jobRestartDelay;
    private Ingestion ingestion;

    private OrderLinkedBlockingQueue<JobClient> queue = null;
    private JobSubmitDealer jobSubmitDealer = null;

    /**
     * 每个GroupPriorityQueue中增加独立线程，以定时调度方式从数据库中获取任务。（数据库查询以id和优先级为条件）
     *
     * @param jobResource
     * @param ingestion
     */
    public GroupPriorityQueue(String jobResource, int queueSizeLimited, long jobRestartDelay, Ingestion ingestion) {
        this.jobResource = jobResource;
        this.queueSizeLimited = queueSizeLimited;
        this.jobRestartDelay = jobRestartDelay;
        this.ingestion = ingestion;
        this.queue = new OrderLinkedBlockingQueue<>(queueSizeLimited * 2);
        this.jobSubmitDealer = new JobSubmitDealer(this);

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("acquireJob_" + jobResource));
        scheduledService.scheduleWithFixedDelay(
                new AcquireGroupQueueJob(),
                0,
                WAIT_INTERVAL,
                TimeUnit.MILLISECONDS);

        ExecutorService jobSubmitService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory("jobSubmit_" + jobResource));
        jobSubmitService.submit(jobSubmitDealer);
    }

    public void add(JobClient jobClient) throws InterruptedException {
        if (isBlocked()){
            return;
        }
        if (queue.contains(jobClient)) {
            return;
        }

        queue.put(jobClient);
    }

    public void addRestartJob(JobClient jobClient) {
        jobSubmitDealer.tryPutRestartJob(jobClient);
    }

    public OrderLinkedBlockingQueue<JobClient> getQueue() {
        return queue;
    }

    public boolean remove(String jobId) {
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
        boolean blocked = running.get() || queueSize() >= queueSizeLimited;
        if (blocked && !running.get()) {
            running.set(true);
            stopAcquireCount.set(0);
        }
        return blocked;
    }

    private long queueSize() {
        return queue.size() + jobSubmitDealer.getRestartJobQueueSize();
    }

    public void resetStartId() {
        startId.set(0);
    }

    public String getJobResource() {
        return jobResource;
    }

    public int getQueueSizeLimited() {
        return queueSizeLimited;
    }

    public long getJobRestartDelay() {
        return jobRestartDelay;
    }

    private class AcquireGroupQueueJob implements Runnable {

        @Override
        public void run() {

            if (Boolean.FALSE == running.get() && queueSize() >= queueSizeLimited) {
                return;
            }

            /**
             * 如果队列中的任务数量小于 ${GroupPriorityQueue.queueSizeLimited} , 在连续调度了  ${GroupPriorityQueue.STOP_ACQUIRE_LIMITED} 次都没有查询到新的数据，则停止调度
             */
            long limitId = ingestion.ingestion(GroupPriorityQueue.this, startId.get(), queueSizeLimited);
            if (limitId != startId.get()) {
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
