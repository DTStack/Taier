package com.dtstack.engine.master.taskdealer;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.exception.WorkerAccessException;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.ClientArgumentException;
import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.SimpleJobDelay;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.common.queue.OrderLinkedBlockingQueue;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.master.cache.ShardCache;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.queue.GroupInfo;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.master.queue.JobPartitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
public class JobSubmitDealer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitDealer.class);

    /**
     * 用于taskListener处理, 此处为static修饰，全局共用一个
     */
    private static LinkedBlockingQueue<JobClient> submittedQueue = new LinkedBlockingQueue<>();

    private JobPartitioner jobPartitioner;
    private WorkerOperator workerOperator;
    private EngineJobCacheDao engineJobCacheDao;
    private ShardCache shardCache;

    private long jobRestartDelay;
    private long jobLackingDelay;
    private long jobPriorityStep;
    private long jobLackingInterval;
    private long jobSubmitExpired;
    private long jobLackingCountLimited = 3;

    private String localAddress;
    private String jobResource = null;
    private GroupPriorityQueue priorityQueue;
    private OrderLinkedBlockingQueue<JobClient> queue = null;
    private DelayBlockingQueue<SimpleJobDelay<JobClient>> delayJobQueue = null;

    public JobSubmitDealer(String localAddress, GroupPriorityQueue priorityQueue, ApplicationContext applicationContext) {
        this.jobPartitioner = applicationContext.getBean(JobPartitioner.class);
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);
        this.engineJobCacheDao = applicationContext.getBean(EngineJobCacheDao.class);
        this.shardCache = applicationContext.getBean(ShardCache.class);
        EnvironmentContext environmentContext = applicationContext.getBean(EnvironmentContext.class);
        if (null == priorityQueue) {
            throw new RdosDefineException("priorityQueue must not null.");
        }
        if (null == jobPartitioner) {
            throw new RdosDefineException("jobPartitioner must not null.");
        }
        if (null == workerOperator) {
            throw new RdosDefineException("workerOperator must not null.");
        }
        if (null == engineJobCacheDao) {
            throw new RdosDefineException("engineJobCacheDao must not null.");
        }
        if (null == environmentContext) {
            throw new RdosDefineException("environmentContext must not null.");
        }

        jobRestartDelay = environmentContext.getJobRestartDelay();
        jobLackingDelay = environmentContext.getJobLackingDelay();
        jobPriorityStep = environmentContext.getJobPriorityStep();
        jobLackingInterval = environmentContext.getJobLackingInterval();
        jobSubmitExpired = environmentContext.getJobSubmitExpired();
        jobLackingCountLimited = environmentContext.getJobLackingCountLimited();


        this.localAddress = localAddress;
        this.priorityQueue = priorityQueue;
        this.jobResource = priorityQueue.getJobResource();
        this.queue = priorityQueue.getQueue();
        this.delayJobQueue = new DelayBlockingQueue<SimpleJobDelay<JobClient>>(priorityQueue.getQueueSizeLimited());

        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName() + "_" + jobResource + "_DelayJobProcessor"));
        executorService.submit(new RestartJobProcessor());
    }

    private class RestartJobProcessor implements Runnable {
        @Override
        public void run() {
            while (true) {
                SimpleJobDelay<JobClient> simpleJobDelay = null;
                JobClient jobClient = null;
                try {
                    simpleJobDelay = delayJobQueue.take();
                    jobClient = simpleJobDelay.getJob();
                    if (jobClient != null) {
                        engineJobCacheDao.updateStage(jobClient.getTaskId(), EJobCacheStage.PRIORITY.getStage(), localAddress, jobClient.getPriority());
                        jobClient.doStatusCallBack(RdosTaskStatus.WAITENGINE.getStatus());
                        queue.put(jobClient);
                        logger.info("jobId:{} stage:{} take job from delayJobQueue queue size:{} and add to priorityQueue.", jobClient.getTaskId(), simpleJobDelay.getStage(), delayJobQueue.size());
                    }
                } catch (Exception e) {
                    if (simpleJobDelay != null && jobClient != null) {
                        logger.error("jobId:{} stage:{}", jobClient.getTaskId(), simpleJobDelay.getStage(), e);
                    } else {
                        logger.error("{}", e);
                    }
                }
            }
        }
    }

    public boolean tryPutRestartJob(JobClient jobClient) {
        boolean tryPut = delayJobQueue.tryPut(new SimpleJobDelay<>(jobClient, EJobCacheStage.RESTART.getStage(), jobRestartDelay));
        logger.info("jobId:{} {} add job to restart delayJobQueue.", jobClient.getTaskId(), tryPut ? "success" : "failed");
        if (tryPut) {
            //restart的状态修改会在外面处理，这里只需要set stage
            engineJobCacheDao.updateStage(jobClient.getTaskId(), EJobCacheStage.RESTART.getStage(), localAddress, jobClient.getPriority());
        }
        return tryPut;
    }

    private boolean tryPutLackingJob(JobClient jobClient) {
        boolean tryPut = delayJobQueue.tryPut(new SimpleJobDelay<>(jobClient, EJobCacheStage.RESTART.getStage(), jobLackingDelay));
        if (tryPut) {
            jobClient.lackingCountIncrement();
            engineJobCacheDao.updateStage(jobClient.getTaskId(), EJobCacheStage.LACKING.getStage(), localAddress, jobClient.getPriority());
            jobClient.doStatusCallBack(RdosTaskStatus.LACKING.getStatus());
        }
        logger.info("jobId:{} {} add job to lacking delayJobQueue, job's lackingCount:{}.", jobClient.getTaskId(), tryPut ? "success" : "failed", jobClient.getLackingCount());
        return tryPut;
    }


    public int getDelayJobQueueSize() {
        return delayJobQueue.size();
    }

    @Override
    public void run() {
        while (true) {
            try {
                JobClient jobClient = queue.take();
                logger.info("jobId:{} jobResource:{} queue size:{} take job from priorityQueue.", jobClient.getTaskId(), jobResource, queue.size());
                if (checkIsFinished(jobClient.getTaskId())) {
                    shardCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.CANCELED.getStatus());
                    jobClient.doStatusCallBack(RdosTaskStatus.CANCELED.getStatus());
                    logger.info("jobId:{} checkIsFinished is true, job is Finished.", jobClient.getTaskId());
                    continue;
                }
                if (checkJobSubmitExpired(jobClient.getGenerateTime())) {
                    shardCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.AUTOCANCELED.getStatus());
                    jobClient.doStatusCallBack(RdosTaskStatus.AUTOCANCELED.getStatus());
                    engineJobCacheDao.delete(jobClient.getTaskId());
                    logger.info("jobId:{} checkJobSubmitExpired is true, job ignore to submit.", jobClient.getTaskId());
                    continue;
                }
                if (!checkMaxPriority(jobResource, jobClient.getPriority())) {
                    logger.info("jobId:{} checkMaxPriority is false, wait other node job which priority higher.", jobClient.getTaskId());
                    queue.put(jobClient);
                    continue;
                }
                //提交任务
                submitJob(jobClient);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    private boolean checkIsFinished(String jobId) {
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (engineJobCache == null) {
            return true;
        }
        return false;
    }

    private boolean checkJobSubmitExpired(long generateTime) {
        if (jobSubmitExpired <= 0) {
            return false;
        }
        long diff = System.currentTimeMillis() - generateTime;
        return diff > jobSubmitExpired;
    }

    private boolean checkMaxPriority(String jobResource, long localPriority) {
        Map<String, GroupInfo> groupInfoMap = jobPartitioner.getGroupInfoByJobResource(jobResource);
        if (null == groupInfoMap) {
            return true;
        }
        for (Map.Entry<String, GroupInfo> groupInfoEntry : groupInfoMap.entrySet()) {
            String address = groupInfoEntry.getKey();
            GroupInfo groupInfo = groupInfoEntry.getValue();
            if (localAddress.equals(address)) {
                continue;
            }
            //Priority值越低，优先级越高
            if (groupInfo.getPriority() > 0 && groupInfo.getPriority() < localPriority) {
                return false;
            }
        }
        return true;
    }

    private void submitJob(JobClient jobClient) {

        JobResult jobResult = null;
        try {

            // 判断资源
            if (workerOperator.judgeSlots(jobClient)) {
                logger.info("jobId:{} engineType:{} submit jobClient:{} to engine start.", jobClient.getTaskId(), jobClient.getEngineType(), jobClient);

                jobClient.doStatusCallBack(RdosTaskStatus.COMPUTING.getStatus());

                // 提交任务
                jobResult = workerOperator.submitJob(jobClient);

                logger.info("jobId:{} engineType:{} submit jobResult:{}.", jobClient.getTaskId(), jobClient.getEngineType(), jobResult);

                String jobId = jobResult.getData(JobResult.JOB_ID_KEY);
                jobClient.setEngineTaskId(jobId);
                addToTaskListener(jobClient, jobResult);
                logger.info("jobId:{} engineType:{} submit to engine end.", jobClient.getTaskId(), jobClient.getEngineType());
            } else {
                logger.info("jobId:{} engineType:{} judgeSlots result is false.", jobClient.getTaskId(), jobClient.getEngineType());
                handlerNoResource(jobClient);
            }
        } catch (WorkerAccessException e) {
            logger.info(" jobId:{} engineType:{} worker not find.", jobClient.getTaskId(), jobClient.getEngineType());
            handlerNoResource(jobClient);
        } catch (ClientAccessException | ClientArgumentException | LimitResourceException e) {
            logger.error("jobId:{} engineType:{} submitJob happens system error:", jobClient.getTaskId(), jobClient.getEngineType(), e);
            jobClient.setEngineTaskId(null);
            jobResult = JobResult.createErrorResult(false, e);
            addToTaskListener(jobClient, jobResult);
        } catch (Throwable e) {
            logger.error("jobId:{} engineType:{} submitJob happens unknown error:", jobClient.getTaskId(), jobClient.getEngineType(), e);
            //捕获未处理异常,防止跳出执行线程
            jobClient.setEngineTaskId(null);
            jobResult = JobResult.createErrorResult(true, e);
            addToTaskListener(jobClient, jobResult);
        }
    }

    private void handlerNoResource(JobClient jobClient) {
        //因为资源不足提交任务失败，优先级数值增加 WAIT_INTERVAL
        jobClient.setPriority(jobClient.getPriority() + jobPriorityStep);

        //delayQueue的任务比重过大时，直接放入优先级队列重试
        if (jobClient.lackingCountIncrement() > jobLackingCountLimited && delayJobQueue.size() < priorityQueue.getQueueSizeLimited()) {
            tryPutLackingJob(jobClient);
        } else {
            try {
                queue.put(jobClient);
                Thread.sleep(jobLackingInterval);
                logger.info("jobId:{} unlimited_lackingCount:{} add to priorityQueue.", jobClient.getTaskId(), jobClient.getLackingCount());
            } catch (Exception e) {
                logger.error("jobId:{} engineType:{} handlerNoResource happens error:", jobClient.getTaskId(), jobClient.getEngineType(), e);
                tryPutLackingJob(jobClient);
            }
        }
    }

    private void addToTaskListener(JobClient jobClient, JobResult jobResult) {
        jobClient.setJobResult(jobResult);
        //添加触发读取任务状态消息
        submittedQueue.offer(jobClient);
    }

    public static LinkedBlockingQueue<JobClient> getSubmittedQueue() {
        return submittedQueue;
    }
}