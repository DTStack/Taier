package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBack;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBackMethod;
import com.dtstack.rdos.engine.execution.base.components.SlotNoAvailableJobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.sql.parser.SqlParser;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 发送具体任务线程
 * Date: 2017/11/27
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class JobSubmitProcessor implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitProcessor.class);

    private JobClient jobClient;

    private Map<String, IClient> clientMap;

    private Map<String, EngineResourceInfo> slotsInfo;

    private SlotNoAvailableJobClient slotNoAvailableJobClient;

    private ClassLoaderCallBackMethod<Object> classLoaderCallBackMethod = new ClassLoaderCallBackMethod<>();

    public JobSubmitProcessor(JobClient jobClient, Map<String, IClient> clientMap,
                              Map<String, EngineResourceInfo> slotsInfo,
                              SlotNoAvailableJobClient slotNoAvailableJobClient) throws Exception{
        this.jobClient = jobClient;
        this.clientMap = clientMap;
        this.slotsInfo = slotsInfo;
        this.slotNoAvailableJobClient = slotNoAvailableJobClient;
    }

    @Override
    public void run(){
        if(jobClient != null){

            Map<String, Integer> updateStatus = Maps.newHashMap();
            updateStatus.put(JobClientCallBack.JOB_STATUS, RdosTaskStatus.WAITCOMPUTE.getStatus());

            jobClient.getJobClientCallBack().execute(updateStatus);
            IClient clusterClient = clientMap.get(jobClient.getEngineType());
            JobResult jobResult = null;

            if(clusterClient == null){
                jobResult = JobResult.createErrorResult("setting client type " +
                        "(" + jobClient.getEngineType()  +") don't found.");
                listenerJobStatus(jobClient, jobResult);
                return;
            }

            try {
                jobClient.setConfProperties(PublicUtil.stringToProperties(jobClient.getTaskParams()));
                //FIXME 修改获取资源--每次提交之前都刷新资源状态
                //EngineResourceInfo resourceInfo = slotsInfo.get(jobClient.getEngineType());
                EngineResourceInfo resourceInfo = clusterClient.getAvailSlots();

                if(resourceInfo.judgeSlots(jobClient)){
                    if(logger.isInfoEnabled()){
                        logger.info("--------submit job:{} to engine start----.", jobClient.getTaskId());
                    }

                    updateStatus.put(JobClientCallBack.JOB_STATUS, RdosTaskStatus.SUBMITTING.getStatus());
                    jobClient.getJobClientCallBack().execute(updateStatus);
                    jobClient.setOperators(SqlParser.parser(jobClient.getEngineType(), jobClient.getComputeType().getComputeType(), jobClient.getSql()));

                    if(logger.isInfoEnabled()){
                        logger.info("-----jobInfo---->"+jobClient.toString());
                    }

                    jobResult = clusterClient.submitJob(jobClient);

                    if(logger.isInfoEnabled()){
                        logger.info("submit job result is:{}.", jobResult);
                    }

                    String jobId = jobResult.getData(JobResult.JOB_ID_KEY);
                    jobClient.setEngineTaskId(jobId);
                }

            }catch (Throwable e){
                //捕获未处理异常,防止跳出执行线程
                jobClient.setEngineTaskId(null);
                jobResult = JobResult.createErrorResult(e);
                logger.error("get unexpected exception", e);
            }finally {
                jobClient.setJobResult(jobResult);
                slotNoAvailableJobClient.put(jobClient);
                if(logger.isInfoEnabled()){
                    logger.info("--------submit job:{} to engine end----", jobClient.getTaskId());
                }
            }
        }
    }

    private void listenerJobStatus(JobClient jobClient, JobResult jobResult){
        jobClient.setJobResult(jobResult);
        JobClient.getQueue().offer(jobClient);//添加触发读取任务状态消息
    }
}
