package com.dtstack.rdos.engine.service.task;

import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.ClientCache;
import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.EJobCacheStage;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.base.restart.IRestartStrategy;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.enums.SourceType;
import com.dtstack.rdos.engine.service.node.WorkNode;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import com.dtstack.rdos.engine.service.zk.cache.ZkLocalCache;
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

    private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();

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
        //update retry num
        increaseJobRetryNum(jobClient.getTaskId(), jobClient.getComputeType().getType());
        LOG.info("------ job: {} add into orderLinkedBlockingQueue again.", jobClient.getTaskId());
        return true;
    }

    private boolean checkNeedReSubmitForSubmitResult(JobClient jobClient){
        if(jobClient.getJobResult() == null){
            //未提交过
            return true;
        }

        if(!jobClient.getJobResult().isErr()){
            return false;
        }

        String engineType = jobClient.getEngineType();

        try{
            String pluginInfo = jobClient.getPluginInfo();
            String resultMsg = jobClient.getJobResult().getMsgInfo();

            IClient client = clientCache.getClient(engineType, pluginInfo);
            if(client == null){
                LOG.error("can't get client by engineType:{}", engineType);
                return false;
            }

            if(!jobClient.getIsFailRetry()){
                return false;
            }

            IRestartStrategy restartStrategy = client.getRestartStrategy();
            if(restartStrategy == null){
                LOG.warn("engineType " + engineType + " not support restart." );
                return false;
            }

            Integer alreadyRetryNum = getAlreadyRetryNum(jobClient.getTaskId(), jobClient.getComputeType().getType());
            return restartStrategy.retrySubmitFail(jobClient.getTaskId(), resultMsg, alreadyRetryNum, null);
        }catch (Exception e){
            LOG.error("", e);
        }

        return false;
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

            Integer alreadyRetryNum = getAlreadyRetryNum(jobId, computeType);
            if(!checkNeedResubmit(jobId, engineJobId, engineType, pluginInfo, computeType, alreadyRetryNum, null)){
                return false;
            }

            RdosEngineJobCache jobCache = engineJobCacheDAO.getJobById(jobId);
            if(jobCache == null){
                LOG.error("can't get record from rdos_engine_job_cache by jobId:{}", jobId);
                return false;
            }

            String jobInfo = jobCache.getJobInfo();
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);
            JobClient jobClient = new JobClient(paramAction);
            String finalJobId = jobClient.getTaskId();
            Integer finalComputeType = jobClient.getComputeType().getType();
            String zkTaskId = TaskIdUtil.getZkTaskId(computeType, engineType, jobId);
            jobClient.setCallBack((jobStatus)->{
                zkLocalCache.updateLocalMemTaskStatus(zkTaskId, jobStatus);
                updateJobStatus(finalJobId, finalComputeType, jobStatus);
            });

            resetStatus(jobId, computeType, engineType);
            addToRestart(jobClient);
            // update retryNum
            increaseJobRetryNum(jobId, computeType);
            LOG.warn("jobName:{}---jobId:{} resubmit again...",jobClient.getJobName(), jobClient.getTaskId());
            return true;
        } catch (Exception e) {
            LOG.error("", e);
            return false;
        }
    }

    private boolean checkNeedResubmit(String jobId,
                                      String engineJobId,
                                      String engineType,
                                      String pluginInfo,
                                      Integer computeType,
                                      Integer alreadyRetryNum,
                                      Integer maxRetryNum) throws Exception {
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

        IRestartStrategy restartStrategy = client.getRestartStrategy();
        if(restartStrategy == null){
            LOG.warn("engineType " + engineType + " not support restart." );
            return false;
        }

        RdosEngineJobCache jobCache = engineJobCacheDAO.getJobById(jobId);
        if(jobCache == null){
            LOG.error("can't get record from rdos_engine_job_cache by jobId:{}", jobId);
            return false;
        }

        String jobInfo = jobCache.getJobInfo();
        ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);
        JobClient jobClient = new JobClient(paramAction);

        if(!jobClient.getIsFailRetry()){
            return false;
        }

        return restartStrategy.checkCanRestart(jobId, engineJobId, client, alreadyRetryNum, maxRetryNum);
    }

    private void resetStatus(String jobId, Integer computeType, String engineType){
        //重试的时候，更改cache状态
        WorkNode.getInstance().saveCache(jobId, engineType, computeType, EJobCacheStage.IN_PRIORITY_QUEUE.getStage(),null, null);
        String zkTaskId = TaskIdUtil.getZkTaskId(computeType, engineType, jobId);
        //重试任务更改在zk的状态，统一做状态清理
        zkLocalCache.updateLocalMemTaskStatus(zkTaskId, RdosTaskStatus.RESTARTING.getStatus());

        if(ComputeType.STREAM.getType().equals(computeType)){
            engineStreamJobDAO.updateTaskEngineIdAndStatus(jobId, null, null, RdosTaskStatus.RESTARTING.getStatus());
            engineStreamJobDAO.updateSubmitLog(jobId, null);
            engineStreamJobDAO.updateEngineLog(jobId, null);
        }else if(ComputeType.BATCH.getType().equals(computeType)){
            engineBatchJobDAO.updateJobEngineIdAndStatus(jobId, null, RdosTaskStatus.RESTARTING.getStatus());
            engineBatchJobDAO.updateSubmitLog(jobId, null);
            engineBatchJobDAO.updateEngineLog(jobId, null);
        }else{
            LOG.error("not support for computeType:{}", computeType);
        }
    }

    private void updateJobStatus(String jobId, Integer computeType, Integer status) {
        if (ComputeType.STREAM.getType().equals(computeType)) {
            engineStreamJobDAO.updateTaskStatus(jobId, status);
        } else {
            engineBatchJobDAO.updateJobStatus(jobId, status);
        }
    }

    private void addToRestart(JobClient jobClient){
        jobClient.setRestartTime(System.currentTimeMillis() + SUBMIT_INTERVAL);
        WorkNode.getInstance().redirectSubmitJob(jobClient);
    }

    /**
     * 获取任务已经重试的次数
     * @param jobId
     * @param computeType
     * @return
     */
    private Integer getAlreadyRetryNum(String jobId, Integer computeType){
        if (ComputeType.STREAM.getType().equals(computeType)) {
            RdosEngineStreamJob rdosEngineStreamJob = engineStreamJobDAO.getRdosTaskByTaskId(jobId);
            return rdosEngineStreamJob.getRetryNum() == null ? 0 : rdosEngineStreamJob.getRetryNum();
        } else {
            RdosEngineBatchJob rdosEngineBatchJob = engineBatchJobDAO.getRdosTaskByTaskId(jobId);
            return rdosEngineBatchJob.getRetryNum() == null ? 0 : rdosEngineBatchJob.getRetryNum();
        }
    }

    private void increaseJobRetryNum(String jobId, Integer computeType){
        if (ComputeType.STREAM.getType().equals(computeType)) {
            RdosEngineStreamJob rdosEngineStreamJob = engineStreamJobDAO.getRdosTaskByTaskId(jobId);
            Integer retryNum = rdosEngineStreamJob.getRetryNum() == null ? 0 : rdosEngineStreamJob.getRetryNum();
            retryNum++;
            engineStreamJobDAO.updateRetryNum(jobId, retryNum);
        } else {
            RdosEngineBatchJob rdosEngineBatchJob = engineBatchJobDAO.getRdosTaskByTaskId(jobId);
            Integer retryNum = rdosEngineBatchJob.getRetryNum() == null ? 0 : rdosEngineBatchJob.getRetryNum();
            retryNum++;
            engineBatchJobDAO.updateRetryNum(jobId, retryNum);
        }
    }
}
