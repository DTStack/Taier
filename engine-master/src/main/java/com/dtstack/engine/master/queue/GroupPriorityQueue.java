package com.dtstack.engine.master.queue;

import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.taskdealer.JobSubmitDealer;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.queue.OrderLinkedBlockingQueue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/16
 */
public class GroupPriorityQueue {

    private static final Logger logger = LoggerFactory.getLogger(GroupPriorityQueue.class);

    private static final int WAIT_INTERVAL = 5000;

    private AtomicBoolean blocked = new AtomicBoolean(false);
    private AtomicBoolean running = new AtomicBoolean(true);

    private AtomicLong startId = new AtomicLong(0);

    private String jobResource;
    private int queueSizeLimited;

    private ApplicationContext applicationContext;
    private EnvironmentContext environmentContext;
    private EngineJobCacheDao engineJobCacheDao;
    private EngineJobDao engineJobDao;
    private WorkNode workNode;
    private JobPartitioner jobPartitioner;
    private WorkerOperator workerOperator;

    private OrderLinkedBlockingQueue<JobClient> queue = null;
    private JobSubmitDealer jobSubmitDealer = null;

    private GroupPriorityQueue() {
    }

    public boolean add(JobClient jobClient, boolean judgeBlock) throws InterruptedException {
        if (judgeBlock) {
            if (isBlocked()) {
                logger.info("jobId:{} unable add to queue, because running queue is blocked.", jobClient.getTaskId());
                return false;
            }
            return addInner(jobClient);
        } else {
            return addRedirect(jobClient);
        }
    }

    private boolean addInner(JobClient jobClient) throws InterruptedException {
        if (this.priorityQueueSize() >= getQueueSizeLimited()) {
            blocked.set(true);
            running.set(false);
            logger.info("jobId:{} unable add to queue, because over QueueSizeLimited, set blocked=true running=false.", jobClient.getTaskId());
            return false;
        }
        return addRedirect(jobClient);
    }

    private boolean addRedirect(JobClient jobClient) throws InterruptedException {
        if (queue.contains(jobClient)) {
            logger.info("jobId:{} unable add to queue, because jobId already exist.", jobClient.getTaskId());
            return true;
        }

        queue.put(jobClient);
        logger.info("jobId:{} redirect add job to queue.", jobClient.getTaskId());
        workNode.updateCache(jobClient, EJobCacheStage.PRIORITY.getStage());
        return true;
    }

    public boolean addRestartJob(JobClient jobClient) {
        return jobSubmitDealer.tryPutRestartJob(jobClient);
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

    private boolean isBlocked() {
        return blocked.get();
    }

    private long priorityQueueSize() {
        return queue.size() + jobSubmitDealer.getDelayJobQueueSize();
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

    private class AcquireGroupQueueJob implements Runnable {

        @Override
        public void run() {

            if (Boolean.FALSE == blocked.get()) {
                return;
            }

            long tmpQueueSize = queue.size();
            int halfQueueSize = getQueueSizeLimited() >> 1;

            if (Boolean.FALSE == running.get()) {
                if (tmpQueueSize < halfQueueSize) {
                    Long jobSize = engineJobCacheDao.countByJobResource(jobResource, EJobCacheStage.DB.getStage(), environmentContext.getLocalAddress());
                    if (jobSize > 0) {
                        running.set(true);
                        blocked.set(true);
                    } else {
                        blocked.set(false);
                        return;
                    }
                } else {
                    return;
                }
            }

            /**
             * 如果队列中的任务数量小于
             * @see com.dtstack.engine.master.queue.GroupPriorityQueue#queueSizeLimited ,
             * 并且没有查询到新的数据，则停止调度
             * @see com.dtstack.engine.master.queue.GroupPriorityQueue#running
             */
            if (tmpQueueSize < getQueueSizeLimited()) {
                long limitId = emitJob2PriorityQueue(startId.get());
                if (limitId == startId.get()) {
                    running.set(false);
                    if (GroupPriorityQueue.this.priorityQueueSize() >= getQueueSizeLimited()) {
                        blocked.set(true);
                    } else {
                        blocked.set(false);
                    }
                    logger.info("Pause AcquireGroupQueueJob running...");
                }
                startId.set(limitId);
            }
        }
    }

    private Long emitJob2PriorityQueue(long startId) {
        String localAddress = environmentContext.getLocalAddress();
        try {
            outLoop:
            while (true) {
                List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(startId, localAddress, EJobCacheStage.DB.getStage(), jobResource);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    break;
                }
                for (EngineJobCache jobCache : jobCaches) {
                    try {
                        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                        JobClient jobClient = new JobClient(paramAction);
                        jobClient.setCallBack((jobStatus) -> {
                            workNode.updateJobStatus(jobClient.getTaskId(), jobStatus);
                        });

                        boolean addInner = this.addInner(jobClient);
                        logger.info("jobId:{} load from db, {} emit job to queue.", jobClient.getTaskId(), addInner ? "success" : "failed");
                        if (!addInner) {
                            break outLoop;
                        }
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        logger.error("", e);
                        //数据转换异常--打日志
                        workNode.dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("emitJob2PriorityQueue localAddress:{} error:", localAddress, e);
        }
        return startId;
    }

    public GroupPriorityQueue setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    public GroupPriorityQueue setJobResource(String jobResource) {
        this.jobResource = jobResource;
        return this;
    }

    public GroupPriorityQueue setWorkNode(WorkNode workNode) {
        this.workNode = workNode;
        return this;
    }

    public static GroupPriorityQueue builder() {
        return new GroupPriorityQueue();
    }

    private void checkParams() {
        if (StringUtils.isBlank(jobResource)) {
            throw new RuntimeException("jobResource is null.");
        }
        if (queueSizeLimited <= 0) {
            throw new RuntimeException("queueSizeLimited less than 0.");
        }
        if (null == environmentContext) {
            throw new RuntimeException("environmentContext is null.");
        }
        if (null == engineJobCacheDao) {
            throw new RuntimeException("engineJobCacheDao is null.");
        }
        if (null == engineJobDao) {
            throw new RuntimeException("engineJobDao is null.");
        }
        if (null == workNode) {
            throw new RuntimeException("workNode is null.");
        }
        if (null == jobPartitioner) {
            throw new RuntimeException("jobPartitioner is null.");
        }
        if (null == workerOperator) {
            throw new RuntimeException("workerOperator is null.");
        }
    }

    /**
     * 每个GroupPriorityQueue中增加独立线程，以定时调度方式从数据库中获取任务。（数据库查询以id和优先级为条件）
     */
    public GroupPriorityQueue build() {
        this.environmentContext = applicationContext.getBean(EnvironmentContext.class);
        this.engineJobCacheDao = applicationContext.getBean(EngineJobCacheDao.class);
        this.engineJobDao = applicationContext.getBean(EngineJobDao.class);
        this.jobPartitioner = applicationContext.getBean(JobPartitioner.class);
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);

        this.queueSizeLimited = environmentContext.getQueueSize();

        checkParams();

        this.queue = new OrderLinkedBlockingQueue<>(queueSizeLimited * 2);
        this.jobSubmitDealer = new JobSubmitDealer(environmentContext.getLocalAddress(), this, applicationContext);

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName() + "_" + jobResource + "_AcquireJob"));
        scheduledService.scheduleWithFixedDelay(
                new AcquireGroupQueueJob(),
                WAIT_INTERVAL * 10,
                WAIT_INTERVAL,
                TimeUnit.MILLISECONDS);

        ExecutorService jobSubmitService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory(this.getClass().getSimpleName() + "_" + jobResource + "_JobSubmit"));
        jobSubmitService.submit(jobSubmitDealer);
        return this;
    }
}
