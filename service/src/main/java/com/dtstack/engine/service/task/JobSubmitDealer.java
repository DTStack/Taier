package com.dtstack.engine.service.task;

import com.dtstack.engine.common.ClientCache;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.IClient;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.ClientArgumentException;
import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.common.queue.OrderLinkedBlockingQueue;
import com.dtstack.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.engine.service.node.WorkNode;
import com.dtstack.engine.service.queue.GroupInfo;
import com.dtstack.engine.service.queue.GroupPriorityQueue;
import com.dtstack.engine.service.queue.SimpleJobDelay;
import com.dtstack.engine.service.zk.ZkDistributed;
import com.dtstack.engine.service.zk.cache.ZkLocalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务提交执行容器
 * 单独起线程执行
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public class JobSubmitDealer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitDealer.class);

    private static final long PRIORITY_STEP = ConfigParse.getJobPriorityStep();
    private static final long JOB_LACKING_INTERVAL = ConfigParse.getJobLackingInterval();
    private static final long JOB_SUBMIT_EXPIRED = ConfigParse.getJobSubmitExpired();

    /**
     * 用于taskListener处理
     */
    private static LinkedBlockingQueue<JobClient> submittedQueue = new LinkedBlockingQueue<>();

    private String localAddress;
    private String jobResource;
    private String engineType;
    private String groupName;
    private GroupPriorityQueue priorityQueue;
    private OrderLinkedBlockingQueue<JobClient> queue = null;
    private DelayBlockingQueue<SimpleJobDelay<JobClient>> delayJobQueue = null;

    private RdosEngineJobCacheDAO rdosEngineJobCacheDao = new RdosEngineJobCacheDAO();
    private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();


    public JobSubmitDealer(String jobResource, String engineType, String groupName, GroupPriorityQueue priorityQueue) {
        this.localAddress = ZkDistributed.getZkDistributed().getLocalAddress();
        this.jobResource = jobResource;
        this.engineType = engineType;
        this.groupName = groupName;
        this.priorityQueue = priorityQueue;
        this.queue = priorityQueue.getQueue();
        this.delayJobQueue = new DelayBlockingQueue<SimpleJobDelay<JobClient>>(priorityQueue.getQueueSizeLimited());

        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName() + "_" + engineType + "_" + groupName + "_DelayJobProcessor"));
        executorService.submit(new DelayJobProcessor());
    }

    private class DelayJobProcessor implements Runnable {
        @Override
        public void run() {
            while (true) {
                SimpleJobDelay<JobClient> simpleJobDelay = null;
                JobClient jobClient = null;
                try {
                    simpleJobDelay = delayJobQueue.take();
                    jobClient = simpleJobDelay.getJob();
                    if (jobClient != null) {
                        WorkNode.getInstance().updateCache(jobClient, EJobCacheStage.PRIORITY.getStage());
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
        boolean tryPut = delayJobQueue.tryPut(new SimpleJobDelay<>(jobClient, EJobCacheStage.RESTART.getStage(), priorityQueue.getJobRestartDelay()));
        logger.info("jobId:{} {} add job to restart delayJobQueue.", jobClient.getTaskId(), tryPut ? "success" : "failed");
        if (tryPut) {
            //restart的状态修改会在外面处理，这里只需要set stage
            WorkNode.getInstance().updateCache(jobClient, EJobCacheStage.RESTART.getStage());
        }
        return tryPut;
    }

    private boolean tryPutLackingJob(JobClient jobClient) {
        boolean tryPut = delayJobQueue.tryPut(new SimpleJobDelay<>(jobClient, EJobCacheStage.LACKING.getStage(), priorityQueue.getJobLackingDelay()));
        if (tryPut) {
            jobClient.lackingCountIncrement();
            WorkNode.getInstance().updateCache(jobClient, EJobCacheStage.LACKING.getStage());
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
                    zkLocalCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.CANCELED.getStatus());
                    jobClient.doStatusCallBack(RdosTaskStatus.CANCELED.getStatus());
                    logger.info("jobId:{} checkIsFinished is true, job is Finished.", jobClient.getTaskId());
                    continue;
                }
                if (checkJobSubmitExpired(jobClient.getGenerateTime())) {
                    zkLocalCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.AUTOCANCELED.getStatus());
                    rdosEngineJobCacheDao.deleteJob(jobClient.getTaskId());
                    jobClient.doStatusCallBack(RdosTaskStatus.AUTOCANCELED.getStatus());
                    logger.info("jobId:{} checkJobSubmitExpired is true, job ignore to submit.", jobClient.getTaskId());
                    continue;
                }
                if (!checkMaxPriority(jobResource, engineType, groupName, jobClient.getPriority())) {
                    logger.info("jobId:{} checkMaxPriority is false, wait other node job which priority higher.", jobClient.getTaskId());
                    queue.put(jobClient);
                    Thread.sleep(JOB_LACKING_INTERVAL);
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
        RdosEngineJobCache engineJobCache = rdosEngineJobCacheDao.getJobById(jobId);
        if (engineJobCache == null) {
            return true;
        }
        return false;
    }

    private boolean checkJobSubmitExpired(long generateTime) {
        if (JOB_SUBMIT_EXPIRED <= 0) {
            return false;
        }
        long diff = System.currentTimeMillis() - generateTime;
        return diff > JOB_SUBMIT_EXPIRED;
    }

    private boolean checkMaxPriority(String jobResource, String engineType, String groupName, long localPriority) {
        Map<String, GroupInfo> groupInfoMap = getGroupInfoByJobResource(jobResource);
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

    public Map<String, GroupInfo> getGroupInfoByJobResource(String jobResource) {
        Map<String, Map<String, GroupInfo>> allNodesGroupQueueJobResources = QueueListener.getAllNodesGroupQueueInfo();
        if (allNodesGroupQueueJobResources.isEmpty()) {
            return null;
        }
        Map<String, GroupInfo> nodesGroupQueue = allNodesGroupQueueJobResources.get(jobResource);
        if (nodesGroupQueue == null || nodesGroupQueue.isEmpty()) {
            return null;
        }
        List<String> aliveBrokers = ZkDistributed.getZkDistributed().getAliveBrokersChildren();
        //将不存活节点过滤
        Iterator<Map.Entry<String, GroupInfo>> nodesGroupQueueIt = nodesGroupQueue.entrySet().iterator();
        while (nodesGroupQueueIt.hasNext()) {
            Map.Entry<String, GroupInfo> groupInfoEntry = nodesGroupQueueIt.next();
            String nodeAddress = groupInfoEntry.getKey();
            if (!aliveBrokers.contains(nodeAddress)) {
                nodesGroupQueueIt.remove();
            }
        }
        return nodesGroupQueue;
    }

    private void submitJob(JobClient jobClient) {
        JobResult jobResult = null;
        try {
            IClient clusterClient = ClientCache.getInstance().getClient(jobClient.getEngineType(), jobClient.getPluginInfo());

            if (clusterClient == null) {
                jobResult = JobResult.createErrorResult("client type (" + jobClient.getEngineType() + ") don't found.");
                addToTaskListener(jobClient, jobResult);
                return;
            }

            if (clusterClient.judgeSlots(jobClient)) {
                logger.info("--------submit job:{} to engine start----.", jobClient.toString());

                jobClient.doStatusCallBack(RdosTaskStatus.COMPUTING.getStatus());

                jobResult = clusterClient.submitJob(jobClient);

                logger.info("submit job result is:{}.", jobResult);

                String jobId = jobResult.getData(JobResult.JOB_ID_KEY);
                jobClient.setEngineTaskId(jobId);
                addToTaskListener(jobClient, jobResult);
                logger.info("--------submit job:{} to engine end----", jobClient.getTaskId());
            } else {
                logger.info(" jobId:{} engineType:{} judgeSlots result is false", jobClient.getTaskId(), jobClient.getEngineType());
                handlerNoResource(jobClient);
            }
        } catch (ClientAccessException | ClientArgumentException | LimitResourceException e) {
            logger.error("get unexpected exception", e);
            jobClient.setEngineTaskId(null);
            jobResult = JobResult.createErrorResult(false, e);
            addToTaskListener(jobClient, jobResult);
        } catch (Throwable e) {
            logger.error("get unexpected exception", e);
            //捕获未处理异常,防止跳出执行线程
            jobClient.setEngineTaskId(null);
            jobResult = JobResult.createErrorResult(true, e);
            addToTaskListener(jobClient, jobResult);
        }
    }

    private void handlerNoResource(JobClient jobClient) {
        //因为资源不足提交任务失败，优先级数值增加 WAIT_INTERVAL
        jobClient.setPriority(jobClient.getPriority() + PRIORITY_STEP);

        //delayQueue的任务比重过大时，直接放入优先级队列重试
        if (jobClient.isLackLimited() && delayJobQueue.size() < priorityQueue.getQueueSizeLimited()) {
            tryPutLackingJob(jobClient);
        } else {
            try {
                queue.put(jobClient);
                Thread.sleep(JOB_LACKING_INTERVAL);
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

