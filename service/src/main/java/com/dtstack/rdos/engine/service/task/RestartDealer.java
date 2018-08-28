package com.dtstack.rdos.engine.service.task;

import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.service.enums.SourceType;
import com.dtstack.rdos.engine.service.node.WorkNode;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.ClientCache;
import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.restart.RestartStrategyUtil;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 注意如果是由于资源不足导致的任务失败应该减慢发送速度
 * Date: 2018/3/22
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RestartDealer {

    private static final Logger LOG = LoggerFactory.getLogger(RestartDealer.class);

    private static final Integer SUBMIT_INTERVAL = 2 * 60 * 1000;

    private RdosEngineJobCacheDAO engineJobCacheDAO = new RdosEngineJobCacheDAO();

    private RdosEngineBatchJobDAO engineBatchJobDAO = new RdosEngineBatchJobDAO();

    private RdosEngineStreamJobDAO engineStreamJobDAO = new RdosEngineStreamJobDAO();

    private ClientCache clientCache = ClientCache.getInstance();

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private static RestartDealer sigleton = new RestartDealer();

    private RestartDealer(){
    }

    public static RestartDealer getInstance(){
        return sigleton;
    }


    /**
     * 对提交结果判定是否重试
     * 不限制重试次数
     * @param jobClient
     * @return
     */
    public boolean checkAndRestartForSubmitResult(JobClient jobClient){
        if(!checkNeedReSubmitForSubmitResult(jobClient)){
            return false;
        }

        resetStatus(jobClient.getTaskId(), jobClient.getComputeType().getType(), jobClient.getEngineType());
        addToRestart(jobClient);
        LOG.info("------ job: {} add into orderLinkedBlockingQueue again.", jobClient.getTaskId());
        return true;
    }

    /***
     * 对任务状态判断是否需要重试
     * @param status
     * @param jobId
     * @param engineJobId
     * @param engineType
     * @param computeType
     * @param pluginInfo
     * @return
     */
    public boolean checkAndRestart(Integer status, String jobId, String engineJobId, String engineType,
                                          Integer computeType, String pluginInfo){

        if(!RdosTaskStatus.FAILED.getStatus().equals(status) && !RdosTaskStatus.SUBMITFAILD.getStatus().equals(status)){
            return false;
        }

        try {
            if(!checkNeedResubmit(jobId, engineJobId, engineType, pluginInfo, computeType)){
                return false;
            }

            resetStatus(jobId, computeType, engineType);
            RdosEngineJobCache jobCache = engineJobCacheDAO.getJobById(jobId);
            if(jobCache == null){
                LOG.error("can't get record from rdos_engine_job_cache by jobId:{}", jobId);
                return false;
            }

            String jobInfo = jobCache.getJobInfo();
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);
            JobClient jobClient = new JobClient(paramAction);
            addToRestart(jobClient);
            LOG.warn("jobName:{}---jobId:{} resubmit again...",jobClient.getJobName(), jobClient.getTaskId());
            return true;
        } catch (Exception e) {
            LOG.error("", e);
            return false;
        }
    }

    private boolean checkNeedResubmit(String jobId, String engineJobId, String engineType, String pluginInfo, Integer computeType) throws Exception {
        if(Strings.isNullOrEmpty(engineJobId)){
            return false;
        }

        if(ComputeType.STREAM.getType().equals(computeType)){
            //do nothing
        }else{
            //需要判断数据来源-->临时查询不需重跑
            RdosEngineBatchJob engineBatchJob = engineBatchJobDAO.getRdosTaskByTaskId(jobId);
            if(engineBatchJob == null){
                LOG.error("batch job {} can't find.", jobId);
                return false;
            }

            if(engineBatchJob.getSourceType() != null && SourceType.TEMP_QUERY.getType().equals(engineBatchJob.getSourceType())){
                return false;
            }

        }

        IClient client = clientCache.getClient(engineType, pluginInfo);
        if(client == null){
            LOG.error("can't get client by engineType:{}", engineJobId);
            return false;
        }

        return RestartStrategyUtil.getInstance().checkCanRestart(jobId, engineJobId, engineType, client);
    }


    private boolean checkNeedReSubmitForSubmitResult(JobClient jobClient){

        if(jobClient.getJobResult() == null){
            //未提交过
            return true;
        }

        return retrySubmitFail(jobClient);
    }

    private boolean isSubmitFail(JobClient jobClient){

        if(jobClient.getJobResult() != null && jobClient.getJobResult().isErr()){
            return true;
        }

        return false;
    }

    private boolean retrySubmitFail(JobClient jobClient){

        if(!isSubmitFail(jobClient)){
            return false;
        }

        try{
            String engineType = jobClient.getEngineType();
            String resultMsg = jobClient.getJobResult().getMsgInfo();
            return RestartStrategyUtil.getInstance().retrySubmitFail(jobClient.getTaskId(), engineType, resultMsg);

        }catch (Exception e){
            LOG.error("", e);
        }

        return false;

    }

    private void resetStatus(String jobId, Integer computeType, String engineType){
        //更新rdos_engine_batch_task/rdos_engine_stream_task 状态
        //清理engineJobId , 更新db/zk状态为waitCompute
        String zkTaskId = TaskIdUtil.getZkTaskId(computeType, engineType, jobId);
        zkDistributed.updateJobZKStatus(zkTaskId, RdosTaskStatus.WAITENGINE.getStatus());
        if(ComputeType.STREAM.getType().equals(computeType)){
            engineStreamJobDAO.updateTaskEngineIdAndStatus(jobId, null, RdosTaskStatus.WAITENGINE.getStatus());
            engineStreamJobDAO.updateSubmitLog(jobId, null);
            engineStreamJobDAO.updateEngineLog(jobId, null);
        }else if(ComputeType.BATCH.getType().equals(computeType)){
            engineBatchJobDAO.updateJobEngineIdAndStatus(jobId, null, RdosTaskStatus.WAITENGINE.getStatus());
            engineBatchJobDAO.updateSubmitLog(jobId, null);
            engineBatchJobDAO.updateEngineLog(jobId, null);
        }else{
            LOG.error("not support for computeType:{}", computeType);
        }
    }

    private void addToRestart(JobClient jobClient){
        jobClient.setRestartTime(System.currentTimeMillis() + SUBMIT_INTERVAL);
        WorkNode.getInstance().addSubmitJob(jobClient);
    }
}
