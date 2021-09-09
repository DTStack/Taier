package com.dtstack.engine.master;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.pojo.CheckResult;
import com.dtstack.engine.common.pojo.ClusterResource;
import com.dtstack.engine.common.pojo.ComponentTestResult;
import com.dtstack.engine.common.pojo.DtScriptAgentLabel;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.dtstack.engine.common.constrant.ConfigConstant.DEPLOY_MODEL;

@Component
public class WorkerOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerOperator.class);

    @Autowired
    private PluginWrapper pluginWrapper;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ClientOperator clientOperator;

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
        return clientOperator.judgeSlots(jobClient);
    }

    public JobResult submitJob(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return clientOperator.submitJob(jobClient);
    }

    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) {
        RdosTaskStatus status = clientOperator.getJobStatus(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == status) {
            status = RdosTaskStatus.NOTFOUND;
        }
        return status;
    }

    @Deprecated
    public String getEngineMessageByHttp(String engineType, String path, String pluginInfo) {
        return "";
    }

    public String getEngineLog(JobIdentifier jobIdentifier) {
        String engineLog = clientOperator.getEngineLog(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == engineLog) {
            engineLog = org.apache.commons.lang3.StringUtils.EMPTY;
        }
        return engineLog;
    }

    public String getCheckpoints(JobIdentifier jobIdentifier) {
        String checkPoints = clientOperator.getCheckpoints(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == checkPoints) {
            checkPoints = org.apache.commons.lang3.StringUtils.EMPTY;
        }
        return checkPoints;
    }

    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        List<String> rollingLogBaseInfo = clientOperator.getRollingLogBaseInfo(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == rollingLogBaseInfo || rollingLogBaseInfo.size() == 0) {
            rollingLogBaseInfo = Lists.newArrayList();
        }
        return rollingLogBaseInfo;
    }

    public String getJobMaster(JobIdentifier jobIdentifier) throws Exception {
        String jobMaster = clientOperator.getJobMaster(jobIdentifier.getEngineType(), this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == jobMaster) {
            jobMaster = org.apache.commons.lang3.StringUtils.EMPTY;
        }
        return jobMaster;
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return clientOperator.stopJob(jobClient);
    }

    public List<String> containerInfos(JobClient jobClient) {
        this.buildPluginInfo(jobClient);
        try {
            List<String> containerInfos = clientOperator.containerInfos(jobClient);
            if (null == containerInfos) {
                containerInfos = new ArrayList<>(0);
            }
            return containerInfos;
        } catch (Exception e) {
            LOGGER.error("getCheckpoints failed!", e);
            return null;
        }
    }

    public ComponentTestResult testConnect(String engineType, String pluginInfo) {
        ComponentTestResult testResult = clientOperator.testConnect(engineType, pluginInfo);
        if (null == testResult) {
            testResult = new ComponentTestResult();
        }
        return testResult;
    }


    public List<List<Object>> executeQuery(String engineType, String pluginInfo, String sql, String database) throws Exception {
        return clientOperator.executeQuery(engineType, pluginInfo, sql, database);
    }

    public String uploadStringToHdfs(String engineType, String pluginInfo, String bytes, String hdfsPath) throws Exception {
        return clientOperator.uploadStringToHdfs(engineType, pluginInfo, bytes, hdfsPath);
    }

    public ClusterResource clusterResource(String engineType, String pluginInfo) throws Exception {
        return clientOperator.getClusterResource(engineType, pluginInfo);
    }

    public CheckResult grammarCheck(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return clientOperator.grammarCheck(jobClient);
    }

    public List<DtScriptAgentLabel> getDtScriptAgentLabel(String engineType,String pluginInfo) throws Exception {
        return clientOperator.getDtScriptAgentLabel(engineType,pluginInfo);
    }
}
