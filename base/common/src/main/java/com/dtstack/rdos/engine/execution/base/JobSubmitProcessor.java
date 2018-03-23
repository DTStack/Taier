package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.components.SlotNoAvailableJobClient;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.sql.parser.SqlParser;
import com.dtstack.rdos.engine.execution.queue.ExeQueueMgr;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 发送具体任务线程
 * Date: 2017/11/27
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobSubmitProcessor implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitProcessor.class);

    private JobClient jobClient;

    public JobSubmitProcessor(JobClient jobClient) throws Exception{
        this.jobClient = jobClient;
    }

    @Override
    public void run(){
        if(jobClient != null){

            Map<String, Integer> updateStatus = Maps.newHashMap();
            updateStatus.put(JobClientCallBack.JOB_STATUS, RdosTaskStatus.WAITCOMPUTE.getStatus());

            jobClient.doJobClientCallBack(updateStatus);
            JobResult jobResult = null;

            try {
                IClient clusterClient = ClientCache.getInstance().getClient(jobClient.getEngineType(), jobClient.getPluginInfo());

                if(clusterClient == null){
                    jobResult = JobResult.createErrorResult("client type (" + jobClient.getEngineType()  +") don't found.");
                    addToTaskListener(jobClient, jobResult);
                    return;
                }

                EngineResourceInfo resourceInfo = clusterClient.getAvailSlots();

                if(resourceInfo.judgeSlots(jobClient)){
                    if(logger.isInfoEnabled()){
                        logger.info("--------submit job:{} to engine start----.", jobClient.toString());
                    }

                    updateStatus.put(JobClientCallBack.JOB_STATUS, RdosTaskStatus.SUBMITTED.getStatus());
                    jobClient.doJobClientCallBack(updateStatus);
                    jobClient.setOperators(SqlParser.parser(jobClient.getEngineType(), jobClient.getComputeType().getType(), jobClient.getSql()));

                    jobResult = clusterClient.submitJob(jobClient);

                    if(logger.isInfoEnabled()){
                        logger.info("submit job result is:{}.", jobResult);
                    }

                    String jobId = jobResult.getData(JobResult.JOB_ID_KEY);
                    jobClient.setEngineTaskId(jobId);
                    jobClient.setJobResult(jobResult);
                    addToTaskListener(jobClient, jobResult);
                    if(logger.isInfoEnabled()){
                        logger.info("--------submit job:{} to engine end----", jobClient.getTaskId());
                    }
                }else{
                    //back to wait queue
                    ExeQueueMgr.getInstance().add(jobClient);
                }

            }catch (Throwable e){
                //捕获未处理异常,防止跳出执行线程
                jobClient.setEngineTaskId(null);
                jobResult = JobResult.createErrorResult(e);
                jobClient.setJobResult(jobResult);
                addToTaskListener(jobClient, jobResult);
                logger.error("get unexpected exception", e);
            }
        }
    }

    private void addToTaskListener(JobClient jobClient, JobResult jobResult){
        jobClient.setJobResult(jobResult);
        JobSubmitExecutor.getInstance().addJobIntoTaskListenerQueue(jobClient);//添加触发读取任务状态消息
    }
}
