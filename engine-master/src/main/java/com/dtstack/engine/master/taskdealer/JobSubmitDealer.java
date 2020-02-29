package com.dtstack.engine.master.taskdealer;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.exception.WorkerAccessException;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.ClientArgumentException;
import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.RestartJob;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.common.queue.OrderLinkedBlockingQueue;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.master.queue.GroupInfo;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.master.queue.JobPartitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /***循环间隔时间3s*/
    private static final int WAIT_INTERVAL = 2 * 1000;

    private String localAddress;
    private GroupPriorityQueue priorityQueue;
    private JobPartitioner jobPartitioner;
    private WorkerOperator workerOperator;
    private EngineJobCacheDao engineJobCacheDao;
    private String jobResource = null;
    private OrderLinkedBlockingQueue<JobClient> queue = null;
    private DelayBlockingQueue<RestartJob<JobClient>> restartJobQueue = null;

    public JobSubmitDealer(String localAddress, GroupPriorityQueue priorityQueue, JobPartitioner jobPartitioner, WorkerOperator workerOperator, EngineJobCacheDao engineJobCacheDao) {
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
        this.localAddress = localAddress;
        this.priorityQueue = priorityQueue;
        this.jobPartitioner = jobPartitioner;
        this.workerOperator = workerOperator;
        this.engineJobCacheDao = engineJobCacheDao;
        this.jobResource = priorityQueue.getJobResource();
        this.queue = priorityQueue.getQueue();
        this.restartJobQueue = new DelayBlockingQueue<RestartJob<JobClient>>(priorityQueue.getQueueSizeLimited());

        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName() + "_RestartJobProcessor"));
        executorService.submit(new RestartJobProcessor());
    }

    private class RestartJobProcessor implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    RestartJob<JobClient> restartJob = restartJobQueue.take();
                    JobClient jobClient = restartJob.getJob();
                    if (jobClient != null) {
                        queue.put(jobClient);
                        logger.info("jobId{} take job from restartJobQueue queueSize:{} and add to priorityQueue.", jobClient.getTaskId(), restartJobQueue.size());
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }


    public boolean tryPutRestartJob(JobClient jobClient) {
        boolean tryPut = restartJobQueue.tryPut(new RestartJob<>(jobClient, priorityQueue.getJobRestartDelay()));
        logger.info("jobId{} {} add job to restartJobQueue .", jobClient.getTaskId(), tryPut ? "success" : "failed");
        return tryPut;
    }

    public int getRestartJobQueueSize() {
        return restartJobQueue.size();
    }

    @Override
    public void run() {
        while (true) {
            try {
                JobClient jobClient = queue.take();
                logger.info("jobId{} jobResource:{} queueSize:{} take job from priorityQueue.", jobClient.getTaskId(), jobResource, priorityQueue.queueSize());
                if (checkIsFinished(jobClient.getTaskId())) {
                    logger.info("jobId:{} checkIsFinished is true, job is Finished.", jobClient.getTaskId());
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

            jobClient.doStatusCallBack(RdosTaskStatus.WAITCOMPUTE.getStatus());

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
                jobClient.doStatusCallBack(RdosTaskStatus.WAITENGINE.getStatus());
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
        try {
            //因为资源不足提交任务失败，优先级数值增加 WAIT_INTERVAL
            jobClient.setPriority(jobClient.getPriority() + WAIT_INTERVAL);
            priorityQueue.getQueue().put(jobClient);
        } catch (InterruptedException e) {
            logger.error("jobId:{} engineType:{} handlerNoResource happens error:", jobClient.getTaskId(), jobClient.getEngineType(), e);
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