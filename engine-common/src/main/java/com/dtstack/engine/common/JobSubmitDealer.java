package com.dtstack.engine.common;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.ClientArgumentException;
import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.queue.GroupPriorityQueue;
import com.dtstack.engine.common.queue.OrderLinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
public class JobSubmitDealer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitDealer.class);

    /***循环间隔时间3s*/
    private static final int WAIT_INTERVAL = 2 * 1000;

    private GroupPriorityQueue priorityQueue;

    /**
     * 用于taskListener处理, 此处为static修饰，全局共用一个
     */
    private static LinkedBlockingQueue<JobClient> submittedQueue = new LinkedBlockingQueue<>();

    public JobSubmitDealer(GroupPriorityQueue queue) {
        if (queue == null) {
            throw new RdosDefineException("queue must not null.");
        }
        this.priorityQueue = queue;
    }

    @Override
    public void run() {
        try {
            String jobResource = priorityQueue.getJobResource();
            OrderLinkedBlockingQueue<JobClient> queue = priorityQueue.getQueue();
            while (true) {
                JobClient jobClient = queue.take();
                //重试任务时间未满足条件
                if (jobClient.isJobRetryWaiting()) {
                    continue;
                }
                if (!checkLocalPriorityIsMax(jobResource, jobClient.getPriority())) {
                    break;
                }
                //提交任务
                submitJob(jobClient);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    //TODO ,重构zk优先级方式
    private boolean checkLocalPriorityIsMax(String jobResource, long localPriority) {
//        if (clusterQueueInfo.isEmpty()) {
//            //等待第一次从zk上获取信息
//            return false;
//        }
//        ClusterQueueInfo.EngineTypeQueueInfo zkInfo = clusterQueueInfo.getEngineTypeQueueInfo(engineType);
//        if (zkInfo == null) {
//            return true;
//        }
//        boolean result = true;
//        for (Map.Entry<String, ClusterQueueInfo.GroupQueueInfo> zkInfoEntry : zkInfo.getGroupQueueInfoMap().entrySet()) {
//            String address = zkInfoEntry.getKey();
//            if (localAddress.equals(address)) {
//                continue;
//            }
//            ClusterQueueInfo.GroupQueueInfo groupQueueZkInfo = zkInfoEntry.getValue();
//            Map<String, GroupInfo> remoteQueueInfo = groupQueueZkInfo.getGroupInfo();
//            GroupInfo groupInfo = remoteQueueInfo.getOrDefault(groupName, new GroupInfo());
//            //Priority值越低，优先级越高
//            if (groupInfo.getPriority() > 0 && groupInfo.getPriority() < localPriority) {
//                result = false;
//                break;
//            }
//        }
//        return result;
        return true;
    }

    private void submitJob(JobClient jobClient) {

        JobResult jobResult = null;
        try {
            jobClient.doStatusCallBack(RdosTaskStatus.WAITCOMPUTE.getStatus());
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
                jobClient.doStatusCallBack(RdosTaskStatus.WAITENGINE.getStatus());
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
        try {
            //因为资源不足提交任务失败，优先级数值增加 WAIT_INTERVAL
            jobClient.setPriority(jobClient.getPriority() + WAIT_INTERVAL);
            priorityQueue.getQueue().put(jobClient);
        } catch (InterruptedException e) {
            logger.error("add jobClient: " + jobClient.getTaskId() + " back to queue error:", e);
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