package com.dtstack.engine.master.akka;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobClientCallBack;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.akka.message.*;
import com.dtstack.engine.common.callback.CallBack;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.plugininfo.PluginWrapper;
import com.dtstack.schedule.common.enums.AppType;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Component
public class WorkerOperator {

    private static final Logger logger = LoggerFactory.getLogger(WorkerOperator.class);

    @Autowired
    private MasterServer masterServer;

    @Autowired
    private PluginWrapper pluginWrapper;

    @Autowired
    private ClusterService clusterService;


    private void buildPluginInfo(JobClient jobClient){
        //补充插件配置信息
        try {
            //jobClient中如果有pluginInfo(数据质量)以jobClient自带优先
            JSONObject info = JSONObject.parseObject(jobClient.getPluginInfo());
            if (Objects.nonNull(info) && !info.isEmpty()) {
                return;
            }
            jobClient.setPluginWrapperInfo(pluginWrapper.wrapperPluginInfo(jobClient.getParamAction()));
        } catch (Exception e) {
            logger.error("{} buildPluginInfo failed!",jobClient.getTaskId(), e);
            throw new RdosDefineException("buildPluginInfo error",e);
        }
    }

    private String getPluginInfo(JobIdentifier jobIdentifier){
        if (Objects.nonNull(jobIdentifier)) {
            JSONObject info = JSONObject.parseObject(jobIdentifier.getPluginInfo());
            if (Objects.nonNull(info) && !info.isEmpty()) {
                return jobIdentifier.getPluginInfo();
            }
        }

        if (Objects.isNull(jobIdentifier) || Objects.isNull(jobIdentifier.getEngineType()) || Objects.isNull(jobIdentifier.getTenantId())) {
            logger.error("pluginInfo params lost {}", jobIdentifier);
            throw new RdosDefineException("pluginInfo params lost");
        }
        JSONObject info = clusterService.pluginInfoJSON(jobIdentifier.getTenantId(), jobIdentifier.getEngineType(), jobIdentifier.getUserId(), jobIdentifier.getDeployMode());
        if(Objects.isNull(info)){
            return null;
        }
        return info.toJSONString();
    }

    public JudgeResult judgeSlots(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        if (AkkaConfig.isLocalMode()) {
            return ClientOperator.getInstance().judgeSlots(jobClient);
        }
        Object result = callbackAndReset(jobClient, () -> masterServer.sendMessage(new MessageJudgeSlots(jobClient)));

        if (result instanceof Exception) {
            throw (Exception) result;
        } else {
            return (JudgeResult) result;
        }
    }

    public JobResult submitJob(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        if (AkkaConfig.isLocalMode()){
            return ClientOperator.getInstance().submitJob(jobClient);
        }
        try {
            return (JobResult) callbackAndReset(jobClient, () -> masterServer.sendMessage(new MessageSubmitJob(jobClient)));
        } catch (TimeoutException e) {
            return JobResult.createErrorResult("because lacking resource, submit job failed.");
        }
    }

    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) {
        if (AkkaConfig.isLocalMode()){
            RdosTaskStatus status = ClientOperator.getInstance().getJobStatus(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
            if (null == status) {
                status = RdosTaskStatus.NOTFOUND;
            }
            return status;
        }
        String jobId = jobIdentifier.getEngineJobId();
        if (Strings.isNullOrEmpty(jobId)) {
            throw new RdosDefineException("can't get job of jobId is empty or null!");
        }
        try {
            Object result = masterServer.sendMessage(new MessageGetJobStatus(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier));
            if (result == null) {
                return null;
            }

            return (RdosTaskStatus) result;
        } catch (Exception e) {
            logger.error("getStatus happens error：{}",jobId, e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    @Deprecated
    public String getEngineMessageByHttp(String engineType, String path, String pluginInfo) {
        if (AkkaConfig.isLocalMode()){
            return "";
        }
        String message;
        try {
            message = (String) masterServer.sendMessage(new MessageGetEngineMessageByHttp(engineType, path, pluginInfo));
        } catch (Exception e) {
            message = ExceptionUtil.getErrorMessage(e);
        }
        return message;
    }

    public String getEngineLog(JobIdentifier jobIdentifier) {
        if (AkkaConfig.isLocalMode()){
            String engineLog = ClientOperator.getInstance().getEngineLog(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
            if (null == engineLog) {
                engineLog = org.apache.commons.lang3.StringUtils.EMPTY;
            }
            return engineLog;
        }
        String logInfo;
        if (StringUtils.isNotBlank(jobIdentifier.getEngineJobId())) {
            logger.warn("jobIdentifier:{}", jobIdentifier);
        }
        try {
            logInfo = (String) masterServer.sendMessage(new MessageGetEngineLog(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier));
        } catch (Exception e) {
            logInfo = ExceptionUtil.getErrorMessage(e);
        }
        return logInfo;
    }

    public String getCheckpoints(JobIdentifier jobIdentifier) {
        if (AkkaConfig.isLocalMode()){
            String checkPoints = ClientOperator.getInstance().getCheckpoints(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
            if (null == checkPoints) {
                checkPoints = org.apache.commons.lang3.StringUtils.EMPTY;
            }
            return checkPoints;
        }
        String checkpoints = null;
        try {
            checkpoints = (String) masterServer.sendMessage(new MessageGetCheckpoints(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier));
        } catch (Exception e) {
            logger.error("getCheckpoints failed!", e);
        }
        return checkpoints;
    }

    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        if (AkkaConfig.isLocalMode()) {
            List<String> rollingLogBaseInfo = ClientOperator.getInstance().getRollingLogBaseInfo(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
            if (null == rollingLogBaseInfo || rollingLogBaseInfo.size() == 0) {
                rollingLogBaseInfo = Lists.newArrayList();
            }
            return rollingLogBaseInfo;
        }
        List<String> rollingLogBaseInfo = null;
        try {
            rollingLogBaseInfo = (List<String>) masterServer.sendMessage(new MessageRollingLogBaseInfo(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier));
        } catch (Exception e) {
            logger.error("getRollingLogBaseInfo failed!", e);
        }
        return rollingLogBaseInfo;
    }

    public String getJobMaster(JobIdentifier jobIdentifier) throws Exception {
        if (AkkaConfig.isLocalMode()){
            String jobMaster = ClientOperator.getInstance().getJobMaster(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
            if (null == jobMaster) {
                jobMaster = org.apache.commons.lang3.StringUtils.EMPTY;
            }
            return jobMaster;
        }
        return (String) masterServer.sendMessage(new MessageGetJobMaster(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier));
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        if (AkkaConfig.isLocalMode()){
            return ClientOperator.getInstance().stopJob(jobClient);
        }
        if (jobClient.getEngineTaskId() == null) {
            return JobResult.createSuccessResult(jobClient.getTaskId());
        }
        return (JobResult) masterServer.sendMessage(new MessageStopJob(jobClient));
    }

    public List<String> containerInfos(JobClient jobClient) {
        this.buildPluginInfo(jobClient);
        if (AkkaConfig.isLocalMode()){
            try {
                List<String> containerInfos = ClientOperator.getInstance().containerInfos(jobClient);
                if (null == containerInfos) {
                    containerInfos = new ArrayList<>(0);
                }
                return containerInfos;
            } catch (Exception e) {
                logger.error("getCheckpoints failed!", e);
                return null;
            }
        }
        try {
            return (List<String>) callbackAndReset(jobClient, () -> masterServer.sendMessage(new MessageContainerInfos(jobClient)));
        } catch (Exception e) {
            logger.error("getCheckpoints failed!", e);
            return null;
        }
    }

    public List<ClientTemplate> getDefaultPluginConfig(String engineType, String configType) {
        if (AkkaConfig.isLocalMode()) {
            List<ClientTemplate> defaultPluginConfig = ClientOperator.getInstance().getDefaultPluginConfig(engineType, configType);
            if (CollectionUtils.isEmpty(defaultPluginConfig)) {
                return new ArrayList<>(0);
            }
            return defaultPluginConfig;
        }
        try {
            return (List<ClientTemplate>) masterServer.sendMessage(new MessageGetPluginDefaultConfig(engineType, configType));
        } catch (Exception e) {
            logger.error("getDefaultPluginConfig failed!", e);
            return null;
        }
    }

    public ComponentTestResult testConnect(String engineType, String pluginInfo) {
        if (AkkaConfig.isLocalMode()) {
            ComponentTestResult testResult = ClientOperator.getInstance().testConnect(engineType, pluginInfo);
            if (Objects.isNull(testResult)) {
                testResult = new ComponentTestResult();
            }
            return testResult;
        }
        try {
            return (ComponentTestResult)masterServer.sendMessage(new MessageTestConnectInfo(engineType,pluginInfo));
        } catch (Exception e) {
            logger.error("testConnect failed!", e);
            return null;
        }
    }


    public List<List<Object>> executeQuery(String engineType, String pluginInfo, String sql, String database) throws Exception {
        if (AkkaConfig.isLocalMode()) {
            return ClientOperator.getInstance().executeQuery(engineType, pluginInfo, sql, database);
        }
        return (List<List<Object>>) masterServer.sendMessage(new MessageExecuteQuery(engineType, pluginInfo, sql, database));
    }

    public String uploadStringToHdfs(String engineType, String pluginInfo, String bytes, String hdfsPath) throws Exception {
        if (AkkaConfig.isLocalMode()) {
            return ClientOperator.getInstance().uploadStringToHdfs(engineType, pluginInfo, bytes, hdfsPath);
        }
        return (String) masterServer.sendMessage(new MessageUploadInfo(engineType, pluginInfo, bytes, hdfsPath));
    }

    public ClusterResource clusterResource(String engineType, String pluginInfo) throws Exception {
        if (AkkaConfig.isLocalMode()) {
            return ClientOperator.getInstance().getClusterResource(engineType, pluginInfo);
        }
        return (ClusterResource) masterServer.sendMessage(new MessageResourceInfo(engineType, pluginInfo));
    }

    private <M> M callbackAndReset(JobClient jobClient, CallBack<M> classLoaderCallBack) throws Exception {
        JobClientCallBack callBack = jobClient.getJobCallBack();
        M result = null;
        Exception exception = null;
        try {
            jobClient.setCallBack(null);
            result = classLoaderCallBack.execute();
        } catch (Exception e) {
            exception = e;
        } finally {
            jobClient.setCallBack(callBack);
        }
        if (exception != null) {
            throw exception;
        } else {
            return result;
        }
    }

}
