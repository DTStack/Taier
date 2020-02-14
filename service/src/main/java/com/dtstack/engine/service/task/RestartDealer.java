package com.dtstack.engine.service.task;

import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.ClientCache;
import com.dtstack.engine.common.IClient;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.restart.CommonRestartService;
import com.dtstack.engine.common.restart.IJobRestartStrategy;
import com.dtstack.engine.service.db.dao.RdosEngineJobDAO;
import com.dtstack.engine.service.db.dao.RdosEngineJobRetryDAO;
import com.dtstack.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.engine.service.db.dao.RdosStreamTaskCheckpointDAO;
import com.dtstack.engine.service.db.dataobject.RdosEngineJob;
import com.dtstack.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.engine.service.db.dataobject.RdosEngineJobRetry;
import com.dtstack.engine.service.db.dataobject.RdosStreamTaskCheckpoint;
import com.dtstack.engine.service.node.WorkNode;
import com.dtstack.engine.service.util.TaskIdUtil;
import com.dtstack.engine.service.zk.cache.ZkLocalCache;
import com.google.common.base.Strings;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;


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

    private RdosEngineJobDAO engineBatchJobDAO = new RdosEngineJobDAO();

    private RdosEngineJobRetryDAO engineJobRetryDAO = new RdosEngineJobRetryDAO();

    private RdosStreamTaskCheckpointDAO streamTaskCheckpointDAO = new RdosStreamTaskCheckpointDAO();

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
        if(!checkSubmitResult(jobClient)){
            return false;
        }

        int alreadyRetryNum = getAlreadyRetryNum(jobClient.getTaskId(), jobClient.getComputeType().getType());
        if (alreadyRetryNum >= jobClient.getMaxRetryNum()) {
            LOG.info("[retry=false] jobId:{} alreadyRetryNum:{} maxRetryNum:{}, alreadyRetryNum >= maxRetryNum.", jobClient.getTaskId(), alreadyRetryNum, jobClient.getMaxRetryNum());
            return false;
        }

        resetStatus(jobClient, true);
        //添加到重试队列中
        addToRestart(jobClient);
        LOG.info("【retry=true】 jobId:{} alreadyRetryNum:{} will retry and add into queue again.", jobClient.getTaskId(), alreadyRetryNum);
        //update retryNum
        increaseJobRetryNum(jobClient.getTaskId(), jobClient.getComputeType().getType());
        return true;
    }

    private boolean checkSubmitResult(JobClient jobClient){
        if(jobClient.getJobResult() == null){
            //未提交过
            return true;
        }

        if(!jobClient.getJobResult().getCheckRetry()){
            LOG.info("[retry=false] jobId:{} jobResult.checkRetry:{} jobResult.msgInfo:{} check retry is false.", jobClient.getTaskId(), jobClient.getJobResult().getCheckRetry(), jobClient.getJobResult().getMsgInfo());
            return false;
        }

        if(!jobClient.getIsFailRetry()){
            LOG.info("[retry=false] jobId:{} isFailRetry:{} isFailRetry is false.", jobClient.getTaskId(), jobClient.getIsFailRetry());
            return false;
        }

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
    public boolean checkAndRestart(Integer status, String jobId, String engineJobId, String appId,
                                   String engineType, Integer computeType, String pluginInfo){
        Pair<Boolean, JobClient> checkResult = checkJobInfo(jobId, engineJobId, status);
        if(!checkResult.getKey()){
            return false;
        }
        // 是否需要重新提交
        int alreadyRetryNum = getAlreadyRetryNum(jobId, computeType);
        if (alreadyRetryNum >= jobClient.getMaxRetryNum()) {
            LOG.info("[retry=false] jobId:{} alreadyRetryNum:{} maxRetryNum:{}, alreadyRetryNum >= maxRetryNum.", jobClient.getTaskId(), alreadyRetryNum, jobClient.getMaxRetryNum());
            return false;
        }

        JobClient jobClient = getJobClientWithStrategy(jobId, engineJobId, appId, engineType, pluginInfo, alreadyRetryNum);
        if (jobClient == null) {
            LOG.error("[retry=false] jobId:{} default not retry, because getJobClientWithStrategy is null.", jobId);
            return false;
        }

        resetStatus(jobClient, false);
        //添加到重试队列中
        addToRestart(jobClient);
        LOG.info("【retry=true】 jobId:{} alreadyRetryNum:{} will retry and add into queue again.", jobId, alreadyRetryNum);
        //update retryNum
        increaseJobRetryNum(jobId, computeType);
        return true;
    }

    private JobClient getJobClientWithStrategy(String jobId, String engineJobId, String appId, String engineType, String pluginInfo, int alreadyRetryNum) {
        try {

            IJobRestartStrategy restartStrategy = getRestartStrategy(engineType, pluginInfo, jobId, engineJobId, appId);
            if (restartStrategy == null) {
                //todo
            }

            RdosEngineJobCache jobCache = engineJobCacheDAO.getJobById(jobId);

            String lastRetryParams = "";
            if (alreadyRetryNum > 0)  {
                lastRetryParams = getLastRetryParams(jobId, alreadyRetryNum-1);
            }

            //   根据策略调整参数配置
            String jobInfo =  restartStrategy.restart(jobCache.getJobInfo(), alreadyRetryNum, lastRetryParams);
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);


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
            saveRetryTaskParam(jobId, paramAction.getTaskParams());

            return jobClient;
        } catch (Exception e) {
            LOG.error("", e);
            return null;
        }
    }

    private String getLastRetryParams(String jobId, int retrynum) {
        String taskParams = "";
        try {
            taskParams = engineJobRetryDAO.getRetryTaskParams(jobId, retrynum);
        } catch (Exception e) {
            LOG.error("", e);
        }
        return taskParams;
    }

    private void saveRetryTaskParam(String jobId, String taskParams) {
        try {
            engineBatchJobDAO.updateRetryTaskParams(jobId, taskParams);
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
        CommonRestartService restartService = client.getRestartService();
        return restartService.getAndParseErrorLog(jobId, engineJobId, appId, client);
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
        RdosStreamTaskCheckpoint taskCheckpoint = streamTaskCheckpointDAO.getByTaskId(jobId);
        if(taskCheckpoint != null){
            LOG.info("Set checkpoint path:{}", taskCheckpoint.getCheckpointSavepath());
            jobClient.setExternalPath(taskCheckpoint.getCheckpointSavepath());
        }
    }

    private Pair<Boolean, JobClient> checkJobInfo(String jobId, String engineJobId, Integer status) {
        Pair<Boolean, JobClient> check = new Pair<>(false, null);

        if(!RdosTaskStatus.FAILED.getStatus().equals(status) && !RdosTaskStatus.SUBMITFAILD.getStatus().equals(status)){
            return check;
        }

        if(Strings.isNullOrEmpty(engineJobId)){
            LOG.error("[retry=false] jobId:{} engineJobId is null.", jobId);
            return check;
        }

        RdosEngineJob engineBatchJob = engineBatchJobDAO.getRdosTaskByTaskId(jobId);
        if(engineBatchJob == null){
            LOG.error("[retry=false] jobId:{} get RdosEngineJob is null.", jobId);
            return check;
        }

        RdosEngineJobCache jobCache = engineJobCacheDAO.getJobById(jobId);
        if(jobCache == null){
            LOG.info("[retry=false] jobId:{} get RdosEngineJobCache is null.", jobId);
            return check;
        }

        try {
            String jobInfo = jobCache.getJobInfo();
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);
            JobClient jobClient = new JobClient(paramAction);

            if(!jobClient.getIsFailRetry()){
                LOG.info("[retry=false] jobId:{} isFailRetry:{} isFailRetry is false.", jobClient.getTaskId(), jobClient.getIsFailRetry());
                return check;
            }

            return new Pair<Boolean, JobClient>(true, jobClient);
        } catch (Exception e){
            // 解析任务的jobInfo反序列到ParamAction失败，任务不进行重试.
            LOG.error("[retry=false] jobId:{} default not retry, because getIsFailRetry happens error:{}.", jobId, e);
            return check;
        }
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
        	engineBatchJobDAO.updateJobSubmitFailed(jobId, null, RdosTaskStatus.RESTARTING.getStatus(),null);
        } else {
        	engineBatchJobDAO.updateJobEngineIdAndStatus(jobId, null, RdosTaskStatus.RESTARTING.getStatus(),null);
        }
        
        engineBatchJobDAO.updateSubmitLog(jobId, null);
        engineBatchJobDAO.updateEngineLog(jobId, null);
        engineBatchJobDAO.updateRetryTaskParams(jobId, null);
        engineBatchJobDAO.resetExecTime(jobId);
    }

    private void jobRetryRecord(JobClient jobClient) {
        try {
            RdosEngineJob batchJob = engineBatchJobDAO.getRdosTaskByTaskId(jobClient.getTaskId());
            RdosEngineJobRetry batchJobRetry = RdosEngineJobRetry.toEntity(batchJob, jobClient);
            batchJobRetry.setStatus(RdosTaskStatus.RESTARTING.getStatus().byteValue());
            engineJobRetryDAO.insert(batchJobRetry);
        } catch (Throwable e ){
            LOG.error("{}",e);
        }
    }

    private void updateJobStatus(String jobId, Integer computeType, Integer status) {
        engineBatchJobDAO.updateJobStatus(jobId, status);
        LOG.info("jobId:{} update job status to {}", jobId, status);
    }

    private void addToRestart(JobClient jobClient){
        jobClient.setRestartTime(System.currentTimeMillis() + SUBMIT_INTERVAL);
        WorkNode.getInstance().redirectSubmitJob(jobClient, false);
    }

    /**
     * 获取任务已经重试的次数
     * @param jobId
     * @param computeType
     * @return
     */
    private Integer getAlreadyRetryNum(String jobId, Integer computeType){
        RdosEngineJob rdosEngineBatchJob = engineBatchJobDAO.getRdosTaskByTaskId(jobId);
        return rdosEngineBatchJob.getRetryNum() == null ? 0 : rdosEngineBatchJob.getRetryNum();
    }

    private void increaseJobRetryNum(String jobId, Integer computeType){
        RdosEngineJob rdosEngineBatchJob = engineBatchJobDAO.getRdosTaskByTaskId(jobId);
        Integer retryNum = rdosEngineBatchJob.getRetryNum() == null ? 0 : rdosEngineBatchJob.getRetryNum();
        retryNum++;
        engineBatchJobDAO.updateRetryNum(jobId, retryNum);
    }
}
