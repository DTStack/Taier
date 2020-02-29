package com.dtstack.engine.master.akka;

import akka.actor.ActorSelection;
import akka.pattern.Patterns;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobClientCallBack;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.callback.CallBack;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.akka.message.*;
import com.dtstack.engine.common.exception.WorkerAccessException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.restart.RestartStrategyType;
import com.dtstack.engine.common.util.RandomUtils;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class WorkerOperator {

    private static final Logger logger = LoggerFactory.getLogger(WorkerOperator.class);

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private AkkaWorkerManager akkaWorkerManager;

    private Object sendRequest(Object message) throws Exception {
        String path = RandomUtils.getRandomValueFromMap(akkaWorkerManager.getAvailableWorkers());
        if (null == path) {
            Thread.sleep(10000);
            throw new WorkerAccessException("sleep 10000 ms.");
        }
        ActorSelection actorRef = akkaWorkerManager.getSystem().actorSelection(path);
        Future<Object> future = Patterns.ask(actorRef, message, env.getAkkaAskTimeout());
        Object result = Await.result(future, Duration.create(env.getAkkaAskResultTimeout(), TimeUnit.SECONDS));
        return result;
    }

    public boolean judgeSlots(JobClient jobClient) {
        Object result = null;
        try {
            result = callbackAndReset(jobClient, () -> sendRequest(new MessageJudgeSlots(jobClient)));
        } catch (Exception e) {
            logger.error("jobid:{} judgeSlots failed!", jobClient.getTaskId(), e);
            return false;
        }
        return (boolean) result;
    }

    public JobResult submitJob(JobClient jobClient) throws Exception {
        return (JobResult) callbackAndReset(jobClient, () -> sendRequest(new MessageSubmitJob(jobClient)));
    }

    public RdosTaskStatus getJobStatus(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        if (Strings.isNullOrEmpty(jobId)) {
            throw new RdosDefineException("can't get job of jobId is empty or null!");
        }
        try {
            Object result = sendRequest(new MessageGetJobStatus(engineType, pluginInfo, jobIdentifier));
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
            message = (String) sendRequest(new MessageGetEngineMessageByHttp(engineType, path, pluginInfo));
        } catch (Exception e) {
            message = ExceptionUtil.getErrorMessage(e);
        }
        return message;
    }

    public String getEngineLog(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        String logInfo;
        try {
            logInfo = (String) sendRequest(new MessageGetEngineLog(engineType, pluginInfo, jobIdentifier));
        } catch (Exception e) {
            logInfo = ExceptionUtil.getErrorMessage(e);
        }
        return logInfo;
    }

    public String getCheckpoints(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        String checkpoints = null;
        try {
            checkpoints = (String) sendRequest(new MessageGetCheckpoints(engineType, pluginInfo, jobIdentifier));
        } catch (Exception e) {
            logger.error("getCheckpoints failed!", e);
        }
        return checkpoints;
    }

    public String getJobMaster(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        return (String) sendRequest(new MessageGetJobMaster(engineType, pluginInfo, jobIdentifier));
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        if (jobClient.getEngineTaskId() == null) {
            return JobResult.createSuccessResult(jobClient.getTaskId());
        }
        return (JobResult) sendRequest(new MessageStopJob(jobClient));
    }

    public List<String> containerInfos(JobClient jobClient) {
        try {
            return (List<String>) callbackAndReset(jobClient, () -> sendRequest(new MessageContainerInfos(jobClient)));
        } catch (Exception e) {
            logger.error("getCheckpoints failed!", e);
            return null;
        }
    }

    public RestartStrategyType getRestartStrategyType(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        try {
            return (RestartStrategyType) sendRequest(new MessageGetCheckpoints(engineType, pluginInfo, jobIdentifier));
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
