package com.dtstack.engine.master.akka;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobClientCallBack;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.callback.CallBack;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.akka.message.*;
import com.dtstack.engine.common.pojo.JobResult;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component
public class WorkerOperator {

    private static final Logger logger = LoggerFactory.getLogger(WorkerOperator.class);

    @Autowired
    private MasterServer masterServer;

    public boolean judgeSlots(JobClient jobClient) throws Exception {
        if (AkkaConfig.isLocalMode()) {
            boolean sufficient = ClientOperator.getInstance().judgeSlots(jobClient);
            return sufficient;
        }
        Object result = callbackAndReset(jobClient, () -> masterServer.sendMessage(new MessageJudgeSlots(jobClient)));

        if (result instanceof Exception) {
            throw (Exception) result;
        } else {
            return (boolean) result;
        }
    }

    public JobResult submitJob(JobClient jobClient) throws Exception {
        if (AkkaConfig.isLocalMode()){
            JobResult jobResult = ClientOperator.getInstance().submitJob(jobClient);
            return jobResult;
        }
        try {
            return (JobResult) callbackAndReset(jobClient, () -> masterServer.sendMessage(new MessageSubmitJob(jobClient)));
        } catch (TimeoutException e) {
            return JobResult.createErrorResult("because lacking resource, submit job failed.");
        }
    }

    public RdosTaskStatus getJobStatus(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        if (AkkaConfig.isLocalMode()){
            RdosTaskStatus status = ClientOperator.getInstance().getJobStatus(engineType, pluginInfo, jobIdentifier);
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
            Object result = masterServer.sendMessage(new MessageGetJobStatus(engineType, pluginInfo, jobIdentifier));
            if (result == null) {
                return null;
            }

            return (RdosTaskStatus) result;
        } catch (Exception e) {
            logger.error("getStatus happens error：{}", e);
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

    public String getEngineLog(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        if (AkkaConfig.isLocalMode()){
            String engineLog = ClientOperator.getInstance().getEngineLog(engineType, pluginInfo, jobIdentifier);
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
            logInfo = (String) masterServer.sendMessage(new MessageGetEngineLog(engineType, pluginInfo, jobIdentifier));
        } catch (Exception e) {
            logInfo = ExceptionUtil.getErrorMessage(e);
        }
        return logInfo;
    }

    public String getCheckpoints(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        if (AkkaConfig.isLocalMode()){
            String checkPoints = ClientOperator.getInstance().getCheckpoints(engineType, pluginInfo, jobIdentifier);
            if (null == checkPoints) {
                checkPoints = org.apache.commons.lang3.StringUtils.EMPTY;
            }
            return checkPoints;
        }
        String checkpoints = null;
        try {
            checkpoints = (String) masterServer.sendMessage(new MessageGetCheckpoints(engineType, pluginInfo, jobIdentifier));
        } catch (Exception e) {
            logger.error("getCheckpoints failed!", e);
        }
        return checkpoints;
    }

    public String getJobMaster(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        if (AkkaConfig.isLocalMode()){
            String jobMaster = ClientOperator.getInstance().getJobMaster(engineType, pluginInfo, jobIdentifier);
            if (null == jobMaster) {
                jobMaster = org.apache.commons.lang3.StringUtils.EMPTY;
            }
            return jobMaster;
        }
        return (String) masterServer.sendMessage(new MessageGetJobMaster(engineType, pluginInfo, jobIdentifier));
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        if (AkkaConfig.isLocalMode()){
            JobResult result = ClientOperator.getInstance().stopJob(jobClient);
            return result;
        }
        if (jobClient.getEngineTaskId() == null) {
            return JobResult.createSuccessResult(jobClient.getTaskId());
        }
        return (JobResult) masterServer.sendMessage(new MessageStopJob(jobClient));
    }

    public List<String> containerInfos(JobClient jobClient) {
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

    public String getDefaultPluginConfig(JobClient jobClient) {
        try {
            return (String) callbackAndReset(jobClient, () -> masterServer.sendMessage(new MessageGetPluginDefaultConfig(jobClient)));
        } catch (Exception e) {
            logger.error("getDefaultPluginConfig failed!", e);
            return "";
        }
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
