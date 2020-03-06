package com.dtstack.engine.master.akka;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobClientCallBack;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.callback.CallBack;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.akka.message.*;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.restart.RestartStrategyType;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkerOperator {

    private static final Logger logger = LoggerFactory.getLogger(WorkerOperator.class);

    @Autowired
    private MasterServer masterServer;

    public boolean judgeSlots(JobClient jobClient) throws Exception {
        Object result = callbackAndReset(jobClient, () -> masterServer.sendMessage(new MessageJudgeSlots(jobClient)));

        if (result instanceof Exception){
            throw (Exception) result;
        } else {
            return (boolean) result;
        }
    }

    public JobResult submitJob(JobClient jobClient) throws Exception {
        return (JobResult) callbackAndReset(jobClient, () -> masterServer.sendMessage(new MessageSubmitJob(jobClient)));
    }

    public RdosTaskStatus getJobStatus(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
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
            return RdosTaskStatus.FAILED;
        }
    }

    @Deprecated
    public String getEngineMessageByHttp(String engineType, String path, String pluginInfo) {
        String message;
        try {
            message = (String) masterServer.sendMessage(new MessageGetEngineMessageByHttp(engineType, path, pluginInfo));
        } catch (Exception e) {
            message = ExceptionUtil.getErrorMessage(e);
        }
        return message;
    }

    public String getEngineLog(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        String logInfo;
        try {
            logInfo = (String) masterServer.sendMessage(new MessageGetEngineLog(engineType, pluginInfo, jobIdentifier));
        } catch (Exception e) {
            logInfo = ExceptionUtil.getErrorMessage(e);
        }
        return logInfo;
    }

    public String getCheckpoints(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        String checkpoints = null;
        try {
            checkpoints = (String) masterServer.sendMessage(new MessageGetCheckpoints(engineType, pluginInfo, jobIdentifier));
        } catch (Exception e) {
            logger.error("getCheckpoints failed!", e);
        }
        return checkpoints;
    }

    public String getJobMaster(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        return (String) masterServer.sendMessage(new MessageGetJobMaster(engineType, pluginInfo, jobIdentifier));
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        if (jobClient.getEngineTaskId() == null) {
            return JobResult.createSuccessResult(jobClient.getTaskId());
        }
        return (JobResult) masterServer.sendMessage(new MessageStopJob(jobClient));
    }

    public List<String> containerInfos(JobClient jobClient) {
        try {
            return (List<String>) callbackAndReset(jobClient, () -> masterServer.sendMessage(new MessageContainerInfos(jobClient)));
        } catch (Exception e) {
            logger.error("getCheckpoints failed!", e);
            return null;
        }
    }

    public RestartStrategyType getRestartStrategyType(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        try {
            return (RestartStrategyType) masterServer.sendMessage(new MessageGetCheckpoints(engineType, pluginInfo, jobIdentifier));
        } catch (Exception e) {
            logger.error("getRestartStrategyType failed!", e);
            return RestartStrategyType.NONE;
        }
    }

    private <M> M callbackAndReset(JobClient jobClient, CallBack<M> classLoaderCallBack) throws Exception {
        JobClientCallBack callBack = jobClient.getJobCallBack();
        try {
            jobClient.setCallBack(null);
            return classLoaderCallBack.execute();
        } finally {
            jobClient.setCallBack(callBack);
        }
    }

}
