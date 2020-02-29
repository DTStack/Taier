package com.dtstack.engine.master.queue;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.SentinelType;
import com.dtstack.engine.common.queue.OrderLinkedBlockingQueue;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Job优先级管理队列，job的执行条件分为下列几种：
 * a. 不满足时间条件(SENTINEL.NONE) ===> 不能执行startJob
 * b. 满足时间条件，但不满足依赖条件(SENTINEL.END_DB\SENTINEL.END_QUEUE) ===> 不能执行startJob
 * c. 满足时间条件与所有依赖条件 ===> 能执行startJob
 * <p>
 * <p>
 * <p>
 * 队列存在限制条件，队列容量不足以容纳所有的job，
 * 一旦存在满足执行条件的job时，需要确保及时执行 startJob 操作
 * <p>
 * <p>
 * 1、blocked = false
 * =================================
 * 1.1 SENTINEL.NONE，触发reback，恢复survivor的数据，不改变block和clearQueue。
 * 1.2 SENTINEL.END_DB，触发reback，恢复survivor的数据，并将哨兵重新放入队列中。
 * ps：在reback之后需要thread.sleep，避免空轮训
 * <p>
 * maxId
 * <p>
 * 2、blocked = true（此处哨兵有2种情况，1：最后的db数据，2：队列最后的数据）
 * =================================
 * ***clearQueue=false 阻塞***
 * 2.1 	SENTINEL.NONE，触发reback，恢复survivor的数据，并将block=false，触发queue自动补数据的机制
 * 2.2 	SENTINEL.END_QUEUE(遇到了哨兵2)，则说明队列中的数据已经经过一次判断且没有可执行的有效数据了，则需要再从DB判断后续是否还有可执行的数据。此处就不需要恢复survivor中的数据了，直接清空即可。将block=false，clearQueue=true，触发queue自动补数据的机制
 * <p>
 * <p>
 * ***clearQueue = true, 再次阻塞 或 没有阻塞***
 * 2.3  SENTINEL.NONE，触发reback，不需要恢复survivor，将queue中数据清空，将startId置为第一次阻塞前的startId（即maxId），将clearQueue=false，将block=false，
 * 2.4	SENTINEL.END_QUEUE，则说明是被再次阻塞了，不需要恢复survivor，将queue中数据清空，将block=false，不改变clearQueue，触发queue自动补数据的机制
 * 2.5  SENTINEL.END_DB，则说明是非阻塞，不需要恢复survivor，将queue中数据清空，将startId置为第一次阻塞前的startId（即maxId），将clearQueue=false，将block=false，与2.3是一样操作策略
 * <p>
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
     * 队列容量
     */
    private int queueSizeLimited = 5000;

    private Integer scheduleType;

    private Ingestion ingestion;

    private OrderLinkedBlockingQueue<BatchJobElement> queue = new OrderLinkedBlockingQueue<>();

    private List<BatchJobElement> survivor = new ArrayList<>(queueSizeLimited);

    private AcquireGroupQueueJob acquireGroupQueueJob = new AcquireGroupQueueJob();

    private AtomicBoolean clearQueue = new AtomicBoolean(false);
    /**
     * 是否阻塞过
     */
    private AtomicBoolean blocked = new AtomicBoolean(false);

    private AtomicBoolean tail = new AtomicBoolean(false);

    /**
     * JopPriorityQueue 中增加独立线程，以定时调度方式从数据库中获取任务。（数据库查询以id和优先级为条件）
     *
     * @param scheduleType <= batchJob.type
     * @param ingestion
     */
    public JopPriorityQueue(Integer scheduleType, Long acquireJobInterval, Integer queueSize, Ingestion ingestion) {
        this.acquireJobInterval = acquireJobInterval;
        this.queueSizeLimited = queueSize;
        this.scheduleType = scheduleType;
        this.ingestion = ingestion;
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName() + "_Acquire_" + scheduleType + "_Job"));
        scheduledService.scheduleWithFixedDelay(
                this.acquireGroupQueueJob,
                0,
                this.acquireJobInterval,
                TimeUnit.MILLISECONDS);
    }

    public BatchJobElement takeJob() throws InterruptedException {
        return queue.take();
    }

    public boolean putJob(ScheduleBatchJob scheduleBatchJob) throws InterruptedException {
        if (scheduleBatchJob == null) {
            throw new RuntimeException("scheduleBatchJob is null");
        }
        if (!isBlocked() && queue.size() <= queueSizeLimited) {
            BatchJobElement element = new BatchJobElement(scheduleBatchJob);
            putElement(element);
            return true;
        }
        boolean ok = blocked.compareAndSet(false, true);
        if (logger.isDebugEnabled()) {
            logger.debug(" blocked compareAndSet(false, true) = {} jobId:{}", ok, scheduleBatchJob.getJobId());
        }
        return false;
    }

    public void putSurvivor(BatchJobElement element) throws InterruptedException {
        survivor.add(element);
    }

    public void putSentinel(SentinelType sentinelType) throws InterruptedException {
        if (sentinelType.isSentinel() && tail.compareAndSet(false, true)) {
            BatchJobElement element = new BatchJobElement(sentinelType);
            putElement(element);
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

    public void clearAndTail() {
        queue.clear();
        tail.compareAndSet(true, false);
    }

    public boolean resetTail() {
        return tail.compareAndSet(true, false);
    }

    public void clearAndAllIngestion() {
        tail.set(false);
        blocked.set(false);
        clearQueue.set(false);
        queue.clear();
        this.acquireGroupQueueJob.allIngestion();
    }

    public boolean isBlocked() {
        return blocked.get();
    }

    public void reback(SentinelType sentinelType) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("blocked:{} clearQueue:{} sentinelType:{}", blocked.get(), clearQueue.get(), sentinelType);
        }

        if (!survivor.isEmpty()) {
            //两种情况下可以恢复
            //a. 非阻塞，(1.1) + (1.2)
            //b. 阻塞下，非哨兵且没有清过queue，(2.1)
            boolean recoverSurvivor = !blocked.get() || !sentinelType.isSentinel() && !clearQueue.get();
            if (recoverSurvivor) {
                for (BatchJobElement element : survivor) {
                    putElement(element);
                }
            }
            survivor.clear();
        }
        //判断queue是否存在block
        //如果block=true，需要重置为false，以触发queue补数据的机制
        if (blocked.get()) {
            if (clearQueue.get()) {
                //需要清空队列，(2.4)
                clearAndTail();
                if (sentinelType.isNone()) {
                    //(2.3)
                    acquireGroupQueueJob.bulkIngestion();
                    clearQueue.compareAndSet(true, false);
                }
            } else if (sentinelType.isEndQueue()) {
                //(2.2)
                clearAndTail();
                clearQueue.compareAndSet(false, true);
            }
        } else if (sentinelType.isEndDb() && clearQueue.get()) {
            //(2.5)
            clearAndTail();
            acquireGroupQueueJob.bulkIngestion();
            clearQueue.compareAndSet(true, false);
        }
        blocked.compareAndSet(true, false);
    }

    private class AcquireGroupQueueJob implements Runnable {

        /**
         * 现有数据中，最大值id
         */
        private volatile long lastMaxId;
        /**
         * 上一次查询的id
         */
        private volatile long lastId;

        @Override
        public void run() {
            long theIngestionId = ingestion.ingestion(JopPriorityQueue.this, lastId);
            //返回的 theIngestionId和lastId 相等，可能是因为队列满而提前返回
            //如果队列没有满的情况下，theIngestionId == lastId，则代表没有查询到新的数据
            if (lastId != 0 && theIngestionId == lastId && !isBlocked()) {
                if (theIngestionId == lastMaxId) {
                    //两次大的遍历数据库，没有新的数据产生，CronJobExecutor 可能存在这种情况
                    logger.info("scheduleType:{} startId:{} can't put new data（exclude RestartJob） to process", scheduleType, theIngestionId);
                }
                lastMaxId = theIngestionId;
            }
            lastId = theIngestionId;
        }

        /**
         * 增量模式，传参为 null 则使用上一次遍历时的最小id
         */
        public void bulkIngestion() {
            lastId = lastMaxId;
        }

        /**
         * 全量模式，重置lastId 和 maxId
         */
        public void allIngestion() {
            lastMaxId = 0;
            lastId = lastMaxId;
        }
    }

    public interface Ingestion {

        /**
         * 匿名函数获取 scheduleType 下的任务
         *
         * @param jopPriorityQueue
         * @param startId
         * @return
         */
        Long ingestion(JopPriorityQueue jopPriorityQueue, long startId);
    }
}
