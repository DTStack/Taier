package com.dtstack.engine.worker.service;

import com.dtstack.engine.api.pojo.*;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.api.WorkerApi;
import com.dtstack.engine.common.api.message.*;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.client.ClientOperator;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/9/4 3:33 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class WorkerApiImpl implements WorkerApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerApiImpl.class);

    public static String pluginPath = String.format("%s/pluginLibs", System.getProperty("user.dir"));

    @Override
    public JudgeResult judgeSlots(MessageJudgeSlots messageJudgeSlots) throws Exception {
        LOGGER.info("jobid:{} WorkerApiImpl.judgeSlots",messageJudgeSlots.getJobClient().getTaskId());
        return ClientOperator.getInstance(pluginPath).judgeSlots(messageJudgeSlots.getJobClient());
    }

    @Override
    public JobResult submitJob(MessageSubmitJob messageSubmitJob) throws Exception {
        LOGGER.info("jobid:{} WorkerApiImpl.submitJob",messageSubmitJob.getJobClient().getTaskId());
        return ClientOperator.getInstance(pluginPath).submitJob(messageSubmitJob.getJobClient());
    }

    @Override
    public RdosTaskStatus getJobStatus(MessageGetJobStatus messageGetJobStatus) throws Exception {
        LOGGER.info("WorkerApiImpl.getJobStatus");
        RdosTaskStatus status = ClientOperator.getInstance(pluginPath).getJobStatus(messageGetJobStatus.getEngineType(), messageGetJobStatus.getPluginInfo(), messageGetJobStatus.getJobIdentifier());
        if (null == status) {
            status = RdosTaskStatus.NOTFOUND;
        }
        return status;
    }

    @Override
    public String getEngineMessageByHttp(MessageGetEngineMessageByHttp msg) throws Exception {
        return "";
    }

    @Override
    public String getEngineMessageByHttp(MessageGetEngineLog msg) throws Exception {
        LOGGER.info("WorkerApiImpl.getEngineMessageByHttp");
        String engineLog = ClientOperator.getInstance(pluginPath).getEngineLog(msg.getEngineType(), msg.getPluginInfo(), msg.getJobIdentifier());
        if (null == engineLog) {
            engineLog = StringUtils.EMPTY;
        }
        return engineLog;
    }

    @Override
    public String getCheckpoints(MessageGetCheckpoints msg) throws Exception {
        LOGGER.info("WorkerApiImpl.getCheckpoints");
        String checkPoints = ClientOperator.getInstance(pluginPath).getCheckpoints(msg.getEngineType(), msg.getPluginInfo(), msg.getJobIdentifier());
        if (null == checkPoints) {
            checkPoints = StringUtils.EMPTY;
        }
        return checkPoints;
    }

    @Override
    public List<String> getRollingLogBaseInfo(MessageRollingLogBaseInfo messageRollingLogBaseInfo) throws Exception {
        return null;
    }

    @Override
    public String getJobMaster(MessageGetJobMaster msg) throws Exception {
        LOGGER.info("WorkerApiImpl.getJobMaster");
        String engineLog = ClientOperator.getInstance(pluginPath).getEngineLog(msg.getEngineType(), msg.getPluginInfo(), msg.getJobIdentifier());
        if (null == engineLog) {
            engineLog = StringUtils.EMPTY;
        }
        return engineLog;
    }

    @Override
    public JobResult stopJob(MessageStopJob messageStopJob) throws Exception {
        LOGGER.info("WorkerApiImpl.stopJob");
        return ClientOperator.getInstance(pluginPath).stopJob(messageStopJob.getJobClient());
    }

    @Override
    public List<String> containerInfos(MessageContainerInfos msg) throws Exception {
        LOGGER.info("WorkerApiImpl.containerInfos");
        List<String> containerInfos = ClientOperator.getInstance(pluginPath).containerInfos(msg.getJobClient());
        if (null == containerInfos) {
            containerInfos = new ArrayList<>(0);
        }
        return containerInfos;
    }

//    @Override
//    public List<ClientTemplate> getDefaultPluginConfig(MessageGetPluginDefaultConfig msg) throws Exception {
//        logger.info("WorkerApiImpl.getDefaultPluginConfig");
//        List<ClientTemplate> defaultPluginConfig = ClientOperator.getInstance(msg.getPluginInfo()).getDefaultPluginConfig(msg.getEngineType(), msg.getConfigType());
//        if (null == defaultPluginConfig) {
//            defaultPluginConfig = new ArrayList<>(0);
//        }
//        return defaultPluginConfig;
//    }

    @Override
    public ComponentTestResult testConnect(MessageTestConnectInfo msg) throws Exception {
        LOGGER.info("WorkerApiImpl.testConnect");
        ComponentTestResult execute = ClientOperator.getInstance(pluginPath).testConnect(msg.getEngineType(), msg.getPluginInfo());
        if (null == execute) {
            execute = new ComponentTestResult();
        }
        return execute;
    }

    @Override
    public List<List<Object>> executeQuery(MessageExecuteQuery msg) throws Exception {
        LOGGER.info("WorkerApiImpl.executeQuery");
        List<List<Object>> execute = ClientOperator.getInstance(pluginPath).executeQuery(msg.getEngineType(), msg.getPluginInfo(), msg.getSql(), msg.getDatabase());
        if (null == execute) {
            execute = new ArrayList<>();
        }
        return execute;
    }

    @Override
    public String uploadStringToHdfs(MessageUploadInfo msg) throws Exception {
        LOGGER.info("WorkerApiImpl.uploadStringToHdfs");
        String execute = ClientOperator.getInstance(pluginPath).uploadStringToHdfs(msg.getEngineType(), msg.getPluginInfo(), msg.getBytes(), msg.getHdfsPath());
        if (null == execute) {
            execute = StringUtils.EMPTY;
        }
        return execute;
    }

    @Override
    public ClusterResource clusterResource(MessageResourceInfo msg) throws Exception {
        LOGGER.info("WorkerApiImpl.clusterResource");
        ClusterResource resource = ClientOperator.getInstance(pluginPath).getClusterResource(msg.getEngineType(), msg.getPluginInfo());
        if (null == resource) {
            resource = new ClusterResource();
        }
        return resource;
    }

    @Override
    public List<Column> getAllColumns(MessageAllColumns messageAllColumns) throws Exception {
        LOGGER.info("WorkerApiImpl.getAllColumns");
        List<Column> allColumns = ClientOperator.getInstance(pluginPath).getAllColumns(messageAllColumns.getTableName(),
                messageAllColumns.getSchemaName(),
                messageAllColumns.getDbName(),
                messageAllColumns.getPluginInfo(),
                messageAllColumns.getEngineType());

        if (CollectionUtils.isEmpty(allColumns)) {
            allColumns = Lists.newArrayList();
        }
        return allColumns;
    }

    @Override
    public CheckResult grammarCheck(MessageGrammarCheck messageGrammarCheck) throws Exception {
        LOGGER.info("WorkerApiImpl.grammarCheck");
        return ClientOperator.getInstance(pluginPath).grammarCheck(messageGrammarCheck.getJobClient());
    }

    @Override
    public List<DtScriptAgentLabel> getDtScriptAgentLabel(MessageDtScriptAgentLabel messageDtScriptAgentLabel) throws Exception {
        LOGGER.info("WorkerApiImpl.getDtScriptAgentLabel");
        return ClientOperator.getInstance(pluginPath).getDtScriptAgentLabel(messageDtScriptAgentLabel.getEngineType(), messageDtScriptAgentLabel.getAgentAddress());
    }
}
