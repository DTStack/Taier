package com.dtstack.engine.master.queue;

import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.taskdealer.JobSubmitDealer;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.queue.OrderLinkedBlockingQueue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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

    private static final Logger logger = LoggerFactory.getLogger(GroupPriorityQueue.class);

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
    private EnvironmentContext environmentContext;
    private EngineJobCacheDao engineJobCacheDao;
    private EngineJobDao engineJobDao;
    private WorkNode workNode;
    private JobPartitioner jobPartitioner;

    private OrderLinkedBlockingQueue<JobClient> queue = null;
    private JobSubmitDealer jobSubmitDealer = null;

    private GroupPriorityQueue() {
    }

    public void add(JobClient jobClient) throws InterruptedException {
        if (isBlocked()) {
            logger.info("jobId:{} unable add to queue, because queue is blocked.", jobClient.getTaskId());
            return;
        }
        addRedirect(jobClient);
    }

    private void addRedirect(JobClient jobClient) throws InterruptedException {
        if (queue.contains(jobClient)) {
            logger.info("jobId:{} unable add to queue, because jobId already exist.", jobClient.getTaskId());
            return;
        }

        queue.put(jobClient);
        logger.info("jobId:{} redirect add job to queue.", jobClient.getTaskId());
        workNode.updateCache(jobClient, EJobCacheStage.PRIORITY.getStage());
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

    public long queueSize() {
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

            if (Boolean.FALSE == running.get()) {
                return;
            }

            /**
             * 如果队列中的任务数量小于 ${GroupPriorityQueue.QUEUE_SIZE_LIMITED} , 在连续调度了  ${GroupPriorityQueue.STOP_ACQUIRE_LIMITED} 次都没有查询到新的数据，则停止调度
             */
            if (queueSize() < queueSizeLimited) {
                long limitId = emitJob2PriorityQueue(startId.get());
                if (limitId != startId.get()) {
                    stopAcquireCount.set(0);
                } else if (stopAcquireCount.incrementAndGet() >= STOP_ACQUIRE_LIMITED) {
                    running.set(false);
                }
                startId.set(limitId);
            }
        }
    }

    private Long emitJob2PriorityQueue(long startId){
        String localAddress = environmentContext.getLocalAddress();
        try {
            int count = 0;
            outLoop :
            while (true) {
                List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(startId, localAddress, EJobCacheStage.DB.getStage(), jobResource);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    break;
                }
                for(EngineJobCache jobCache : jobCaches){
                    try {
                        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                        JobClient jobClient = new JobClient(paramAction);
                        jobClient.setCallBack((jobStatus)-> {
                            workNode.updateJobStatus(jobClient.getTaskId(), jobStatus);
                        });

                        this.addRedirect(jobClient);
                        logger.info("jobId:{} load from db, emit job to queue.", jobClient.getTaskId());
                        startId = jobCache.getId();
                        if (++count >= queueSizeLimited){
                            break outLoop;
                        }
                    } catch (Exception e) {
                        //数据转换异常--打日志
                        workNode.dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("emitJob2PriorityQueue error:{}", localAddress, e);
        }
        return startId;
    }



    public GroupPriorityQueue setJobResource(String jobResource) {
        this.jobResource = jobResource;
        return this;
    }

    public GroupPriorityQueue setEnvironmentContext(EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
        return this;
    }

    public GroupPriorityQueue setEngineJobCacheDao(EngineJobCacheDao engineJobCacheDao) {
        this.engineJobCacheDao = engineJobCacheDao;
        return this;
    }

    public GroupPriorityQueue setEngineJobDao(EngineJobDao engineJobDao) {
        this.engineJobDao = engineJobDao;
        return this;
    }

    public GroupPriorityQueue setWorkNode(WorkNode workNode) {
        this.workNode = workNode;
        return this;
    }

    public GroupPriorityQueue setJobPartitioner(JobPartitioner jobPartitioner) {
        this.jobPartitioner = jobPartitioner;
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
        if (jobRestartDelay <= 0) {
            throw new RuntimeException("jobRestartDelay less than 0.");
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
    }

    /**
     * 每个GroupPriorityQueue中增加独立线程，以定时调度方式从数据库中获取任务。（数据库查询以id和优先级为条件）
     */
    public GroupPriorityQueue build(){
        this.queueSizeLimited = environmentContext.getQueueSize();
        this.jobRestartDelay = environmentContext.getJobRestartDelay();

        checkParams();

        this.queue = new OrderLinkedBlockingQueue<>(queueSizeLimited * 2);
        this.jobSubmitDealer = new JobSubmitDealer(environmentContext.getLocalAddress(), this, jobPartitioner, engineJobCacheDao);

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("acquireJob_" + jobResource));
        scheduledService.scheduleWithFixedDelay(
                new AcquireGroupQueueJob(),
                0,
                WAIT_INTERVAL,
                TimeUnit.MILLISECONDS);

        ExecutorService jobSubmitService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory("jobSubmit_" + jobResource));
        jobSubmitService.submit(jobSubmitDealer);
        return this;
    }
}
