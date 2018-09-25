package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * 发送具体任务线程
 * Date: 2017/11/27
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class JobSubmitProcessor implements Callable<JobSubmitProcessor> {

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitProcessor.class);

    private JobClient jobClient;
    private String groupName;
    private String engineType;

    public JobSubmitProcessor(JobClient jobClient, String groupName, String engineType) {
        this.jobClient = jobClient;
        this.groupName = groupName;
        this.engineType = engineType;
    }

    @Override
    public JobSubmitProcessor call() {

        JobResult jobResult = null;
        try {
            jobClient.doStatusCallBack(RdosTaskStatus.WAITCOMPUTE.getStatus());
            IClient clusterClient = ClientCache.getInstance().getClient(jobClient.getEngineType(), jobClient.getPluginInfo());

            if (clusterClient == null) {
                jobResult = JobResult.createErrorResult("client type (" + jobClient.getEngineType() + ") don't found.");
                addToTaskListener(jobClient, jobResult);
                return this;
            }

            EngineResourceInfo resourceInfo = clusterClient.getAvailSlots();

            if (resourceInfo != null && resourceInfo.judgeSlots(jobClient)) {
                if (logger.isInfoEnabled()) {
                    logger.info("--------submit job:{} to engine start----.", jobClient.toString());
                }

                jobClient.doStatusCallBack(RdosTaskStatus.SUBMITTED.getStatus());

                jobResult = clusterClient.submitJob(jobClient);

                if (logger.isInfoEnabled()) {
                    logger.info("submit job result is:{}.", jobResult);
                }

                String jobId = jobResult.getData(JobResult.JOB_ID_KEY);
                jobClient.setEngineTaskId(jobId);
                addToTaskListener(jobClient, jobResult);
                if (logger.isInfoEnabled()) {
                    logger.info("--------submit job:{} to engine end----", jobClient.getTaskId());
                }
                return this;
            }
            return null;
        } catch (Throwable e) {
            //捕获未处理异常,防止跳出执行线程
            jobClient.setEngineTaskId(null);
            jobResult = JobResult.createErrorResult(e);
            addToTaskListener(jobClient, jobResult);
            logger.error("get unexpected exception", e);
            return this;
        }
    }

    private void addToTaskListener(JobClient jobClient, JobResult jobResult) {
        jobClient.setJobResult(jobResult);
        JobSubmitExecutor.getInstance().addJobIntoTaskListenerQueue(jobClient);//添加触发读取任务状态消息
    }

    public JobClient getJobClient() {
        return jobClient;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getEngineType() {
        return engineType;
    }

}
