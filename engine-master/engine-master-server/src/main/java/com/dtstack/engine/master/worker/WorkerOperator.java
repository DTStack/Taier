package com.dtstack.engine.master.worker;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.pojo.DtScriptAgentLabel;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.api.WorkerApi;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.plugininfo.PluginWrapper;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.dtstack.engine.common.constrant.ConfigConstant.DEPLOY_MODEL;

@Component
public class WorkerOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerOperator.class);

    @Autowired
    private WorkerApi workerApi;

    @Autowired
    private PluginWrapper pluginWrapper;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ScheduleDictService scheduleDictService;


    private void buildPluginInfo(JobClient jobClient){
        //补充插件配置信息
        try {
            //jobClient中如果有pluginInfo(数据质量)以jobClient自带优先
            JSONObject info = JSONObject.parseObject(jobClient.getPluginInfo());
            if (null != info && !info.isEmpty()) {
                return;
            }
            Map<String, Object> pluginInfo = pluginWrapper.wrapperPluginInfo(jobClient.getParamAction());
            jobClient.setPluginWrapperInfo(pluginInfo);
            if(pluginInfo.containsKey(DEPLOY_MODEL)){
                jobClient.setDeployMode((Integer) pluginInfo.get(DEPLOY_MODEL));
            }
        } catch (Exception e) {
            LOGGER.error("{} buildPluginInfo failed!",jobClient.getTaskId(), e);
            throw new RdosDefineException("buildPluginInfo error",e);
        }
    }

    private String getPluginInfo(JobIdentifier jobIdentifier){
        if (null != jobIdentifier) {
            JSONObject info = JSONObject.parseObject(jobIdentifier.getPluginInfo());
            if (null != info && !info.isEmpty()) {
                return jobIdentifier.getPluginInfo();
            }
        }

        if (null == jobIdentifier || null == jobIdentifier.getEngineType() || null == jobIdentifier.getTenantId()) {
            LOGGER.error("pluginInfo params lost {}", jobIdentifier);
            throw new RdosDefineException("pluginInfo params lost");
        }
        EngineTypeComponentType engineTypeComponentType = EngineTypeComponentType.getByEngineName(jobIdentifier.getEngineType());
        String componentVersionValue = scheduleDictService.convertVersionNameToValue(jobIdentifier.getComponentVersion(), engineTypeComponentType.getScheduleEngineType().getEngineName());
        JSONObject info = clusterService.pluginInfoJSON(jobIdentifier.getTenantId(), jobIdentifier.getEngineType(), jobIdentifier.getUserId(), jobIdentifier.getDeployMode(),
                Collections.singletonMap(engineTypeComponentType.getComponentType().getTypeCode(),componentVersionValue));
        if(null == info){
            return null;
        }
        return info.toJSONString();
    }

    public JudgeResult judgeSlots(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return workerApi.judgeSlots(new com.dtstack.engine.common.api.message.MessageJudgeSlots(jobClient));
    }

    public JobResult submitJob(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        try {
            return workerApi.submitJob(new com.dtstack.engine.common.api.message.MessageSubmitJob(jobClient));
        } catch (TimeoutException e) {
            return JobResult.createErrorResult("because lacking resource, submit job failed.");
        }
    }

    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        if (Strings.isNullOrEmpty(jobId)) {
            throw new RdosDefineException("can't get job of jobId is empty or null!");
        }
        try {
            RdosTaskStatus result = workerApi.getJobStatus(new com.dtstack.engine.common.api.message.MessageGetJobStatus(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier));
            if (result == null) {
                result = RdosTaskStatus.NOTFOUND;
            }

            return result;
        } catch (Exception e) {
            LOGGER.error("getStatus happens error：{}",jobId, e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    @Deprecated
    public String getEngineMessageByHttp(String engineType, String path, String pluginInfo) {
        String message;
        try {
            message = workerApi.getEngineMessageByHttp(new com.dtstack.engine.common.api.message.MessageGetEngineMessageByHttp(engineType, path, pluginInfo));
        } catch (Exception e) {
            message = ExceptionUtil.getErrorMessage(e);
        }
        return message;
    }

    public String getEngineLog(JobIdentifier jobIdentifier) {
        String logInfo;
        if (StringUtils.isNotBlank(jobIdentifier.getEngineJobId())) {
            LOGGER.warn("jobIdentifier:{}", jobIdentifier);
        }
        try {
            logInfo = workerApi.getEngineMessageByHttp(new com.dtstack.engine.common.api.message.MessageGetEngineLog(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier));
        } catch (Exception e) {
            logInfo = ExceptionUtil.getErrorMessage(e);
        }
        return logInfo;
    }

    public String getCheckpoints(JobIdentifier jobIdentifier) {
        String checkpoints = null;
        try {
            checkpoints = workerApi.getCheckpoints(new com.dtstack.engine.common.api.message.MessageGetCheckpoints(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier));
        } catch (Exception e) {
            LOGGER.error("getCheckpoints failed!", e);
        }
        return checkpoints;
    }

    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        List<String> rollingLogBaseInfo = null;
        try {
            rollingLogBaseInfo = workerApi.getRollingLogBaseInfo(new com.dtstack.engine.common.api.message.MessageRollingLogBaseInfo(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier));
        } catch (Exception e) {
            LOGGER.error("getRollingLogBaseInfo failed!", e);
        }
        return rollingLogBaseInfo;
    }

    public String getJobMaster(JobIdentifier jobIdentifier) throws Exception {
        return workerApi.getJobMaster(new com.dtstack.engine.common.api.message.MessageGetJobMaster(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier));
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        if (jobClient.getEngineTaskId() == null) {
            return JobResult.createSuccessResult(jobClient.getTaskId());
        }
        return workerApi.stopJob(new com.dtstack.engine.common.api.message.MessageStopJob(jobClient));
    }

    public List<String> containerInfos(JobClient jobClient) {
        this.buildPluginInfo(jobClient);
        try {
            return workerApi.containerInfos(new com.dtstack.engine.common.api.message.MessageContainerInfos(jobClient));
        } catch (Exception e) {
            LOGGER.error("getCheckpoints failed!", e);
            return null;
        }
    }

    public ComponentTestResult testConnect(String engineType, String pluginInfo) {
        try {
            return workerApi.testConnect(new com.dtstack.engine.common.api.message.MessageTestConnectInfo(engineType,pluginInfo));
        } catch (Exception e) {
            LOGGER.error("testConnect failed!", e);
            return null;
        }
    }


    public List<List<Object>> executeQuery(String engineType, String pluginInfo, String sql, String database) throws Exception {
        return workerApi.executeQuery(new com.dtstack.engine.common.api.message.MessageExecuteQuery(engineType, pluginInfo, sql, database));
    }

    public String uploadStringToHdfs(String engineType, String pluginInfo, String bytes, String hdfsPath) throws Exception {
        return workerApi.uploadStringToHdfs(new com.dtstack.engine.common.api.message.MessageUploadInfo(engineType, pluginInfo, bytes, hdfsPath));
    }

    public ClusterResource clusterResource(String engineType, String pluginInfo) throws Exception {
        return workerApi.clusterResource(new com.dtstack.engine.common.api.message.MessageResourceInfo(engineType, pluginInfo));
    }


    public CheckResult grammarCheck(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return workerApi.grammarCheck(new com.dtstack.engine.common.api.message.MessageGrammarCheck(jobClient));
    }

    public List<DtScriptAgentLabel> getDtScriptAgentLabel(String engineType,String pluginInfo) throws Exception {
        return workerApi.getDtScriptAgentLabel(new com.dtstack.engine.common.api.message.MessageDtScriptAgentLabel(engineType,pluginInfo));
    }
}
