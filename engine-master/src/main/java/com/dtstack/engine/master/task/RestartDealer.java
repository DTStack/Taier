package com.dtstack.engine.master.task;

import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.ClientCache;
import com.dtstack.engine.common.IClient;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.restart.ARestartService;
import com.dtstack.engine.common.restart.IJobRestartStrategy;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.dao.EngineJobRetryDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.StreamTaskCheckpointDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.domain.EngineJob;
import com.dtstack.engine.domain.EngineJobRetry;
import com.dtstack.engine.domain.StreamTaskCheckpoint;
import com.dtstack.engine.common.util.TaskIdUtil;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.cache.ZkLocalCache;
import com.dtstack.engine.master.resource.JobComputeResourcePlain;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


/**
 * 注意如果是由于资源不足导致的任务失败应该减慢发送速度
 * Date: 2018/3/22
 * Company: www.dtstack.com
 * @author xuchao
 */
@Component
public class RestartDealer {

    private static final Logger LOG = LoggerFactory.getLogger(RestartDealer.class);

    @Autowired
    private JobComputeResourcePlain jobComputeResourcePlain;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EngineJobDao engineJobDao;

    @Autowired
    private EngineJobRetryDao engineJobRetryDao;

    @Autowired
    private StreamTaskCheckpointDao streamTaskCheckpointDao;

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

        resetStatus(jobClient, true);
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

        if(!jobClient.getJobResult().getCheckRetry()){
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

            ARestartService restartStrategy = client.getRestartService();
            if(restartStrategy == null){
                LOG.warn("engineType " + engineType + " not support restart." );
                return false;
            }

            Integer alreadyRetryNum = getAlreadyRetryNum(jobClient.getTaskId(), jobClient.getComputeType().getType());
            return restartStrategy.retrySubmitFail(jobClient.getTaskId(), resultMsg, alreadyRetryNum, jobClient.getMaxRetryNum());
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
    public boolean checkAndRestart(Integer status, String jobId, String engineJobId, String appId, String engineType,
                                          Integer computeType, String pluginInfo){
        if(!RdosTaskStatus.FAILED.getStatus().equals(status) && !RdosTaskStatus.SUBMITFAILD.getStatus().equals(status)){
            return false;
        }
        try {
            Integer alreadyRetryNum = getAlreadyRetryNum(jobId, computeType);
            // 是否需要重新提交
            boolean needResubmit = checkNeedResubmit(jobId, engineJobId, appId, engineType, pluginInfo, computeType, alreadyRetryNum);
            LOG.info("[checkAndRestart] jobId:{} engineJobId:{} status:{} engineType:{} alreadyRetryNum:{} needResubmit:{}",
                                        jobId, engineJobId, status, engineType, alreadyRetryNum, needResubmit);

            if(!needResubmit){
                return false;
            }

            EngineJobCache jobCache = engineJobCacheDao.getOne(jobId);
            if(jobCache == null){
                LOG.error("can't get record from rdos_engine_job_cache by jobId:{}", jobId);
                return false;
            }

            IJobRestartStrategy restartStrategy = getRestartStrategy(engineType, pluginInfo, jobId, engineJobId, appId );

            if (restartStrategy == null) {
                return false;
            }

            String lastRetryParams = "";
            if (alreadyRetryNum > 0)  {
                lastRetryParams = getLastRetryParams(jobId, alreadyRetryNum-1);
            }

            //   根据策略调整参数配置
            String jobInfo =  restartStrategy.restart(jobCache.getJobInfo(), alreadyRetryNum, lastRetryParams);
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);

            saveRetryTaskParam(jobId, paramAction.getTaskParams());

            JobClient jobClient = new JobClient(paramAction);
            String finalJobId = jobClient.getTaskId();
            Integer finalComputeType = jobClient.getComputeType().getType();
            jobClient.setCallBack((jobStatus)->{
                updateJobStatus(finalJobId, finalComputeType, jobStatus);
            });

            if(EngineType.Kylin.name().equalsIgnoreCase(jobClient.getEngineType())){
                setRetryTag(jobClient);
            }

            // checkpoint的路径
            if(EJobType.SYNC.equals(jobClient.getJobType())){
                setCheckpointPath(jobClient);
            }

            resetStatus(jobClient, false);
            //  添加到重试队列中
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

    private String getLastRetryParams(String jobId, int retrynum) {
        String taskParams = "";
        try {
            taskParams = engineJobRetryDao.getRetryTaskParams(jobId, retrynum);
        } catch (Exception e) {
            LOG.error("", e);
        }
        return taskParams;
    }

    private void saveRetryTaskParam(String jobId, String taskParams) {
        try {
            engineJobDao.updateRetryTaskParams(jobId, taskParams);
        } catch (Exception e) {
            LOG.error("saveRetryTaskParam error..", e);
        }
    }

    /**
     *   根据日志提取重试策略
     * @param engineType
     * @param pluginInfo
     * @param engineJobId
     * @return
     * @throws Exception
     */
    private IJobRestartStrategy getRestartStrategy(String engineType, String pluginInfo, String jobId, String engineJobId, String appId) throws Exception {
        IClient client = clientCache.getClient(engineType, pluginInfo);
        ARestartService restartService = client.getRestartService();
        IJobRestartStrategy strategy = restartService.getAndParseErrorLog(jobId, engineJobId, appId, client);
        return strategy;
    }

    private void setRetryTag(JobClient jobClient){
        try {
            Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(jobClient.getPluginInfo(), Map.class);
            pluginInfoMap.put("retry", true);
            jobClient.setPluginInfo(PublicUtil.objToString(pluginInfoMap));
        } catch (IOException e) {
            LOG.warn("Set retry tag error:", e);
        }
    }

    /**
     * 设置这次实例重试的checkpoint path为上次任务实例生成的最后一个checkpoint path
     * @param jobClient client
     */
    private void setCheckpointPath(JobClient jobClient){
        boolean openCheckpoint = Boolean.parseBoolean(jobClient.getConfProperties().getProperty("openCheckpoint"));
        if (!openCheckpoint){
            return;
        }

        String jobId = jobClient.getTaskId();
        if(StringUtils.isEmpty(jobId)){
            return;
        }

        LOG.info("Set checkpoint path for job:{}", jobId);
        StreamTaskCheckpoint taskCheckpoint = streamTaskCheckpointDao.getByTaskId(jobId);
        if(taskCheckpoint != null){
            LOG.info("Set checkpoint path:{}", taskCheckpoint.getCheckpointSavepath());
            jobClient.setExternalPath(taskCheckpoint.getCheckpointSavepath());
        }
    }

    private boolean checkNeedResubmit(String jobId,
                                      String engineJobId,
                                      String appId,
                                      String engineType,
                                      String pluginInfo,
                                      Integer computeType,
                                      Integer alreadyRetryNum) throws Exception {
        if(Strings.isNullOrEmpty(engineJobId)){
            return false;
        }

        if(ComputeType.STREAM.getType().equals(computeType)){
            //do nothing
        }else{
            EngineJob engineBatchJob = engineJobDao.getRdosJobByJobId(jobId);
            if(engineBatchJob == null){
                LOG.error("batch job {} can't find.", jobId);
                return false;
            }
        }

        IClient client = clientCache.getClient(engineType, pluginInfo);
        if(client == null){
            LOG.error("can't get client by engineType:{}", engineJobId);
            return false;
        }

        ARestartService restartService = client.getRestartService();

        if(restartService == null){
            LOG.warn("engineType " + engineType + " not support restart." );
            return false;
        }

        EngineJobCache jobCache = engineJobCacheDao.getOne(jobId);
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
        // 未到达失败重试次数
        return restartService.checkCanRestart(jobId, engineJobId, appId, client, alreadyRetryNum, jobClient.getMaxRetryNum());
    }

    private void resetStatus(JobClient jobClient, boolean submitFailed){
        String jobId = jobClient.getTaskId();
        Integer computeType = jobClient.getComputeType().getType();
        String engineType = jobClient.getEngineType();
        //重试的时候，更改cache状态
        WorkNode.getInstance().updateCache(jobClient, EJobCacheStage.IN_PRIORITY_QUEUE.getStage());
        String zkTaskId = TaskIdUtil.getZkTaskId(computeType, engineType, jobId);
        //重试任务更改在zk的状态，统一做状态清理
        zkLocalCache.updateLocalMemTaskStatus(zkTaskId, RdosTaskStatus.RESTARTING.getStatus());

        //重试的任务不置为失败，waitengine
        jobRetryRecord(jobClient);
        
        if (submitFailed){
        	engineJobDao.updateJobSubmitFailed(jobId, null, RdosTaskStatus.RESTARTING.getStatus(),null);
        } else {
        	engineJobDao.updateJobEngineIdAndStatus(jobId, null, RdosTaskStatus.RESTARTING.getStatus(),null);
        }
        
        engineJobDao.updateSubmitLog(jobId, null);
        engineJobDao.updateEngineLog(jobId, null);
        engineJobDao.updateRetryTaskParams(jobId, null);
        engineJobDao.resetExecTime(jobId);
    }

    private void jobRetryRecord(JobClient jobClient) {
        try {
            EngineJob batchJob = engineJobDao.getRdosJobByJobId(jobClient.getTaskId());
            EngineJobRetry batchJobRetry = EngineJobRetry.toEntity(batchJob, jobClient);
            batchJobRetry.setStatus(RdosTaskStatus.RESTARTING.getStatus().byteValue());
            engineJobRetryDao.insert(batchJobRetry);
        } catch (Throwable e ){
            LOG.error("{}",e);
        }
    }

    private void updateJobStatus(String jobId, Integer computeType, Integer status) {
        engineJobDao.updateJobStatus(jobId, status);
        LOG.info("jobId:{} update job status to {}", jobId, status);
    }

    private void addToRestart(JobClient jobClient){
        WorkNode.getInstance().addRestartJob(jobClient);
    }

    /**
     * 获取任务已经重试的次数
     * @param jobId
     * @param computeType
     * @return
     */
    private Integer getAlreadyRetryNum(String jobId, Integer computeType){
        EngineJob rdosEngineBatchJob = engineJobDao.getRdosJobByJobId(jobId);
        return rdosEngineBatchJob.getRetryNum() == null ? 0 : rdosEngineBatchJob.getRetryNum();
    }

    private void increaseJobRetryNum(String jobId, Integer computeType){
        EngineJob rdosEngineBatchJob = engineJobDao.getRdosJobByJobId(jobId);
        Integer retryNum = rdosEngineBatchJob.getRetryNum() == null ? 0 : rdosEngineBatchJob.getRetryNum();
        retryNum++;
        engineJobDao.updateRetryNum(jobId, retryNum);
    }
}
