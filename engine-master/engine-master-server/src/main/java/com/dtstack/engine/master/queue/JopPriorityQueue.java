package com.dtstack.engine.master.queue;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.queue.OrderLinkedBlockingQueue;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/28
 */
public class JopPriorityQueue {

    private final Logger logger = LoggerFactory.getLogger(JopPriorityQueue.class);

    /**
     * 从DB获取数据的间隔
     */
    private long acquireJobInterval = 3000;

    /**
     * 队列的大小
     */
    private int queueSizeLimited = 5000;

    private Integer scheduleType;

    private Ingestion ingestion;

    private OrderLinkedBlockingQueue<BatchJobElement> queue;

    private AcquireGroupQueueJob acquireGroupQueueJob = new AcquireGroupQueueJob();

    private AtomicBoolean clearQueue = new AtomicBoolean(false);

    /**
     * JopPriorityQueue 中增加独立线程，以定时调度方式从数据库中获取任务。（数据库查询以id和优先级为条件）
     *
     * @param scheduleType <= batchJob.type
     */
    public JopPriorityQueue(Integer scheduleType, Long acquireJobInterval, Integer queueSize, Ingestion ingestion) {
        this.acquireJobInterval = acquireJobInterval;
        this.queueSizeLimited = queueSize;
        this.queue = new OrderLinkedBlockingQueue<>(queueSize);
        this.scheduleType = scheduleType;
        this.ingestion = ingestion;
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName() + "_Acquire_" + scheduleType + "_Job"));
        scheduledService.scheduleWithFixedDelay(
                this.acquireGroupQueueJob,
                0,
                this.acquireJobInterval,
                TimeUnit.MILLISECONDS);
    }

    public int getQueueSize() {
        return this.queue.size();
    }

    public int getFullSize() {
        return queueSizeLimited;
    }


    public BatchJobElement takeJob() throws InterruptedException {
        return queue.take();
    }

    public boolean putJob(ScheduleBatchJob scheduleBatchJob) {
        if (scheduleBatchJob == null) {
            throw new RuntimeException("scheduleBatchJob is null");
        }

        if (clearQueue.get() == Boolean.TRUE) {
            return Boolean.FALSE;
        }

        BatchJobElement element = new BatchJobElement(scheduleBatchJob);

        try {
            return putElement(element);
        } catch (Exception e) {
            logger.info("jobId:{},queue timeout cause interrupted:",scheduleBatchJob.getJobId(),e);
            return Boolean.FALSE;
        }
    }

    private boolean putElement(BatchJobElement element) throws InterruptedException {
        if (element == null) {
            throw new RuntimeException("element is null");
        }
        if (queue.contains(element)) {
            //元素已存在，返回true
            return true;
        }
        queue.put(element);
        return true;
    }

    /**
     * 清空队列，禁止使用 queue.clear()
     *
     * @param clearQueueInterface 关闭队列后执行的逻辑
     */
    public void clearAndAllIngestion(ClearQueue clearQueueInterface) {
        try {
            clearQueue.compareAndSet(Boolean.FALSE,Boolean.TRUE);
            clearQueueInterface.processingStatus(this);
        } finally {
            clearQueue.compareAndSet(Boolean.TRUE,Boolean.FALSE);
        }
    }

    private class AcquireGroupQueueJob implements Runnable {

        @Override
        public void run() {
            ingestion.ingestion(JopPriorityQueue.this);
        }

    }

    public interface Ingestion {

        /**
         * 匿名函数获取 scheduleType 下的任务
         *
         * @param jopPriorityQueue 队列
         */
        void ingestion(JopPriorityQueue jopPriorityQueue);
    }

     public interface ClearQueue {
        /**
         * 匿名函数获取 scheduleType 下的任务
         * 关闭队列时，如何处理队列中剩下的元素
         *
         * @return
         */
        void processingStatus(JopPriorityQueue jopPriorityQueue);
     }


}
