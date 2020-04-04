package com.dtstack.engine.master.taskdealer;

import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.dao.BatchJobDao;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.dao.EngineJobRetryDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.StreamTaskCheckpointDao;
import com.dtstack.engine.domain.EngineJob;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.domain.EngineJobRetry;
import com.dtstack.engine.domain.StreamTaskCheckpoint;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.cache.ShardCache;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
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
public class TaskRestartDealer {

    private static final Logger LOG = LoggerFactory.getLogger(TaskRestartDealer.class);

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EngineJobDao engineJobDao;

    @Autowired
    private EngineJobRetryDao engineJobRetryDao;

    @Autowired
    private StreamTaskCheckpointDao streamTaskCheckpointDao;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private WorkNode workNode;

    @Autowired
    private BatchJobDao batchJobDao;

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

        int alreadyRetryNum = getAlreadyRetryNum(jobClient.getTaskId());
        if (alreadyRetryNum >= jobClient.getMaxRetryNum()) {
            LOG.info("[retry=false] jobId:{} alreadyRetryNum:{} maxRetryNum:{}, alreadyRetryNum >= maxRetryNum.", jobClient.getTaskId(), alreadyRetryNum, jobClient.getMaxRetryNum());
            return false;
        }

        boolean retry = restartJob(jobClient);
        LOG.info("【retry={}】 jobId:{} alreadyRetryNum:{} will retry and add into queue again.", retry, jobClient.getTaskId(), alreadyRetryNum);

        return retry;
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
     * @param pluginInfo
     * @return
     */
    public boolean checkAndRestart(Integer status, String jobId, String engineJobId, String appId, String engineType, String pluginInfo){
        Pair<Boolean, JobClient> checkResult = checkJobInfo(jobId, engineJobId, status);
        if(!checkResult.getKey()){
            return false;
        }

        JobClient jobClient = checkResult.getValue();
        // 是否需要重新提交
        int alreadyRetryNum = getAlreadyRetryNum(jobId);
        if (alreadyRetryNum >= jobClient.getMaxRetryNum()) {
            LOG.info("[retry=false] jobId:{} alreadyRetryNum:{} maxRetryNum:{}, alreadyRetryNum >= maxRetryNum.", jobClient.getTaskId(), alreadyRetryNum, jobClient.getMaxRetryNum());
            return false;
        }

        // 通过engineJobId或appId获取日志
        jobClient.setEngineTaskId(engineJobId);
        jobClient.setApplicationId(appId);

        jobClient.setCallBack((jobStatus)->{
            updateJobStatus(jobId, jobStatus);
        });

        if(EngineType.Kylin.name().equalsIgnoreCase(jobClient.getEngineType())){
            setRetryTag(jobClient);
        }

        //checkpoint的路径
        if(EJobType.SYNC.equals(jobClient.getJobType())){
            setCheckpointPath(jobClient);
        }

        boolean retry = restartJob(jobClient);
        LOG.info("【retry={}】 jobId:{} alreadyRetryNum:{} will retry and add into queue again.", retry, jobClient.getTaskId(), alreadyRetryNum);

        return retry;
    }

    private void setRetryTag(JobClient jobClient){
        try {
            Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(jobClient.getPluginInfo(), Map.class);
            pluginInfoMap.put("retry", true);
            jobClient.setPluginInfo(PublicUtil.objToString(pluginInfoMap));
        } catch (IOException e) {
            LOG.warn("Set retry tag error:{}", e);
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

        if(StringUtils.isEmpty(jobClient.getTaskId())){
            return;
        }

        StreamTaskCheckpoint taskCheckpoint = streamTaskCheckpointDao.getByTaskId(jobClient.getTaskId());
        if(taskCheckpoint != null){
            jobClient.setExternalPath(taskCheckpoint.getCheckpointSavepath());
        }
        LOG.info("jobId:{} set checkpoint path:{}", jobClient.getTaskId(), jobClient.getExternalPath());
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

        EngineJob engineBatchJob = engineJobDao.getRdosJobByJobId(jobId);
        if(engineBatchJob == null){
            LOG.error("[retry=false] jobId:{} get EngineJob is null.", jobId);
            return check;
        }

        EngineJobCache jobCache = engineJobCacheDao.getOne(jobId);
        if(jobCache == null){
            LOG.info("[retry=false] jobId:{} get EngineJobCache is null.", jobId);
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

    private boolean restartJob(JobClient jobClient){
        //添加到重试队列中
        boolean isAdd = workNode.addRestartJob(jobClient);
        if (isAdd) {
            String jobId = jobClient.getTaskId();
            //重试任务更改在zk的状态，统一做状态清理
            shardCache.updateLocalMemTaskStatus(jobId, RdosTaskStatus.RESTARTING.getStatus());

            EngineJob batchJob = engineJobDao.getRdosJobByJobId(jobClient.getTaskId());
            workNode.getAndUpdateEngineLog(jobId, jobClient.getEngineTaskId(), jobClient.getApplicationId(), batchJob.getPluginInfoId());

            //重试的任务不置为失败，waitengine
            jobRetryRecord(jobClient);

            engineJobDao.updateJobUnSubmitOrRestart(jobId, RdosTaskStatus.RESTARTING.getStatus());
            LOG.info("jobId:{} update job status:{}.", jobId, RdosTaskStatus.RESTARTING.getStatus());

            //update retryNum
            increaseJobRetryNum(jobClient.getTaskId());
        }
        return isAdd;
    }

    private void jobRetryRecord(JobClient jobClient) {
        try {
            EngineJob batchJob = engineJobDao.getRdosJobByJobId(jobClient.getTaskId());
            EngineJobRetry batchJobRetry = EngineJobRetry.toEntity(batchJob, jobClient);
            batchJobRetry.setStatus(RdosTaskStatus.RESTARTING.getStatus());
            engineJobRetryDao.insert(batchJobRetry);
        } catch (Throwable e ){
            LOG.error("{}",e);
        }
    }

    private void updateJobStatus(String jobId, Integer status) {
        engineJobDao.updateJobStatus(jobId, status);
        LOG.info("jobId:{} update job status:{}.", jobId, status);
    }

    /**
     * 获取任务已经重试的次数
     */
    private Integer getAlreadyRetryNum(String jobId){
        EngineJob rdosEngineBatchJob = engineJobDao.getRdosJobByJobId(jobId);
        return rdosEngineBatchJob.getRetryNum() == null ? 0 : rdosEngineBatchJob.getRetryNum();
    }

    private void increaseJobRetryNum(String jobId){
        EngineJob rdosEngineBatchJob = engineJobDao.getRdosJobByJobId(jobId);
        Integer retryNum = rdosEngineBatchJob.getRetryNum() == null ? 0 : rdosEngineBatchJob.getRetryNum();
        retryNum++;
        engineJobDao.updateRetryNum(jobId, retryNum);
        batchJobDao.updateJobInfoByJobId(jobId,null,null,null,null,retryNum);
    }
}
