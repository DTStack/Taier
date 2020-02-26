package com.dtstack.engine.common;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import com.dtstack.engine.common.akka.ActorManager;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.message.*;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WorkerOperator {
    private static final Logger LOG = LoggerFactory.getLogger(WorkerOperator.class);

    private ActorSystem system = ActorManager.getInstance().getSystem();

    private Map<String, WorkerInfo> workerActors = ActorManager.getInstance().getWorkerInfoMap();

    private static WorkerOperator singleton = new WorkerOperator();

    public static WorkerOperator getInstance() {
        return singleton;
    }

    private Object sendRequest(Object message) throws Exception {
        String path = RandomUtils.getRandomValueFromMap(workerActors).getPath();
        ActorSelection actorRef = system.actorSelection(path);
        Future<Object> future = Patterns.ask(actorRef, message, 5000);
        Object result = Await.result(future, Duration.create(3, TimeUnit.SECONDS));
        return result;
    }

    public boolean judgeSlots(JobClient jobClient) throws Exception {
        return (boolean) sendRequest(new MessageJudgeSlots(jobClient));
    }

    public JobResult submitJob(JobClient jobClient) throws Exception {
        return (JobResult) sendRequest(new MessageSubmitJob(jobClient));
    }

    public RdosTaskStatus getJobStatus(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        return (RdosTaskStatus) sendRequest(new MessageGetJobStatus(engineType, pluginInfo, jobIdentifier));
    }

    public String getEngineMessageByHttp(String engineType, String path, String pluginInfo) throws Exception {
        return (String) sendRequest(new MessageGetEngineMessageByHttp(engineType, path, pluginInfo));
    }

    public String getEngineLog(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        return (String) sendRequest(new MessageGetEngineLog(engineType, pluginInfo, jobIdentifier));
    }

    public String getCheckpoints(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        return (String) sendRequest(new MessageGetCheckpoints(engineType, pluginInfo, jobIdentifier));
    }

    public String getJobMaster(String engineType, String pluginInfo, JobIdentifier jobIdentifier) throws Exception {
        return (String) sendRequest(new MessageGetJobMaster(engineType, pluginInfo, jobIdentifier));
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        return (JobResult) sendRequest(new MessageStopJob(jobClient));
    }

    public List<String> containerInfos(JobClient jobClient) throws Exception {
        return (List<String>) sendRequest(new MessageContainerInfos(jobClient));
    }
}
