package com.dtstack.engine.service.queue;

import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.queue.OrderLinkedBlockingQueue;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.engine.service.node.WorkNode;
import com.dtstack.engine.service.task.JobSubmitDealer;
import com.dtstack.engine.service.zk.ZkDistributed;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.concurrent.atomic.AtomicLong;

/**
 * 执行引擎对应的优先级队列信息
 * Date: 2018/1/15
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class GroupPriorityQueue {

    private static final Logger logger = LoggerFactory.getLogger(GroupPriorityQueue.class);

    private static final int WAIT_INTERVAL = 5000;
    private static final int QUEUE_SIZE_LIMITED = ConfigParse.getQueueSize();

    private AtomicBoolean blocked = new AtomicBoolean(false);
    private AtomicBoolean running = new AtomicBoolean(true);

    private AtomicLong startId = new AtomicLong(0);

    private String jobResource;
    private String engineType;
    private String groupName;
    private String localAddress;
    private long jobRestartDelay = ConfigParse.getJobRestartDelay();
    private long jobLackingDelay = ConfigParse.getJobLackingDelay();

    private RdosEngineJobCacheDAO rdosEngineJobCacheDAO = new RdosEngineJobCacheDAO();


    private OrderLinkedBlockingQueue<JobClient> queue = null;
    private JobSubmitDealer jobSubmitDealer = null;

    /**
     * 每个GroupPriorityQueue中增加独立线程，以定时调度方式从数据库中获取任务。（数据库查询以id和优先级为条件）
     */
    public GroupPriorityQueue(String jobResource, String engineType, String groupName) {
        this.jobResource = jobResource;
        this.engineType = engineType;
        this.groupName = groupName;

        this.localAddress = ZkDistributed.getZkDistributed().getLocalAddress();

        this.queue = new OrderLinkedBlockingQueue<>(QUEUE_SIZE_LIMITED * 2);
        this.jobSubmitDealer = new JobSubmitDealer(jobResource, engineType, groupName, this);


        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName() + "_" + engineType + "_" + groupName + "_AcquireJob"));
        scheduledService.scheduleWithFixedDelay(
                new AcquireGroupQueueJob(),
                WAIT_INTERVAL * 10,
                WAIT_INTERVAL,
                TimeUnit.MILLISECONDS);

        ExecutorService jobSubmitService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory(this.getClass().getSimpleName() + "_" + engineType + "_" + groupName + "_JobSubmit"));
        jobSubmitService.submit(jobSubmitDealer);
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
        if (this.queueSize() >= getQueueSizeLimited()) {
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
        WorkNode.getInstance().updateCache(jobClient, EJobCacheStage.PRIORITY.getStage());
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

    private long queueSize() {
        return queue.size() + jobSubmitDealer.getDelayJobQueueSize();
    }

    public void resetStartId() {
        startId.set(0);
    }

    public String getJobResource() {
        return jobResource;
    }

    public String getEngineType() {
        return engineType;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getQueueSizeLimited() {
        return QUEUE_SIZE_LIMITED;
    }

    public long getJobRestartDelay() {
        return jobRestartDelay;
    }

    public long getJobLackingDelay() {
        return jobLackingDelay;
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
                    Long jobSize = rdosEngineJobCacheDAO.countByJobResource(engineType, groupName, EJobCacheStage.DB.getStage(), localAddress);
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
             * @see com.dtstack.engine.service.queue.GroupPriorityQueue#QUEUE_SIZE_LIMITED ,
             * 并且没有查询到新的数据，则停止调度
             * @see com.dtstack.engine.service.queue.GroupPriorityQueue#running
             */
            if (tmpQueueSize < getQueueSizeLimited()) {
                long limitId = emitJob2PriorityQueue(startId.get());
                if (limitId == startId.get()) {
                    running.set(false);
                    if (GroupPriorityQueue.this.queueSize() >= getQueueSizeLimited()) {
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
        try {
            outLoop:
            while (true) {
                List<RdosEngineJobCache> jobCaches = rdosEngineJobCacheDAO.listByNodeAddressStage(startId, localAddress, EJobCacheStage.DB.getStage(), engineType, groupName);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    break;
                }
                for (RdosEngineJobCache jobCache : jobCaches) {
                    try {
                        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                        JobClient jobClient = new JobClient(paramAction);
                        jobClient.setCallBack((jobStatus) -> {
                            WorkNode.getInstance().updateJobStatus(jobClient.getTaskId(), jobStatus);
                        });
                        boolean addInner = this.addInner(jobClient);
                        logger.info("jobId:{} load from db, {} emit job to queue.", jobClient.getTaskId(), addInner ? "success" : "failed");
                        if (!addInner) {
                            break outLoop;
                        }
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        //数据转换异常--打日志
                        logger.error("", e);
                        WorkNode.getInstance().dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("emitJob2PriorityQueue localAddress:{} error:", localAddress, e);
        }
        return startId;
    }

}
