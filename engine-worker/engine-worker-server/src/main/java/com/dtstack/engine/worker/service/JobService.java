package com.dtstack.engine.worker.service;

import akka.actor.AbstractActor;
import com.dtstack.engine.common.akka.message.*;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.pojo.JudgeResult;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JobService extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageJudgeSlots.class, msg -> {
                    JudgeResult sufficient = ClientOperator.getInstance().judgeSlots(msg.getJobClient());
                    sender().tell(sufficient, getSelf());
                })
                .match(MessageSubmitJob.class, msg -> {
                    JobResult jobResult = ClientOperator.getInstance().submitJob(msg.getJobClient());
                    sender().tell(jobResult, getSelf());
                })
                .match(MessageGetJobStatus.class, msg -> {
                    RdosTaskStatus status = ClientOperator.getInstance().getJobStatus(msg.getEngineType(), msg.getPluginInfo(), msg.getJobIdentifier());
                    if (null == status) {
                        status = RdosTaskStatus.NOTFOUND;
                    }
                    sender().tell(status, getSelf());
                })
                .match(MessageGetEngineLog.class, msg -> {
                    String engineLog = ClientOperator.getInstance().getEngineLog(msg.getEngineType(), msg.getPluginInfo(), msg.getJobIdentifier());
                    if (null == engineLog) {
                        engineLog = StringUtils.EMPTY;
                    }
                    sender().tell(engineLog, getSelf());
                })
                .match(MessageGetJobMaster.class, msg -> {
                    String jobMaster = ClientOperator.getInstance().getJobMaster(msg.getEngineType(), msg.getPluginInfo(), msg.getJobIdentifier());
                    if (null == jobMaster) {
                        jobMaster = StringUtils.EMPTY;
                    }
                    sender().tell(jobMaster, getSelf());
                })
                .match(MessageStopJob.class, msg -> {
                    JobResult result = ClientOperator.getInstance().stopJob(msg.getJobClient());
                    sender().tell(result, getSelf());

                })
                .match(MessageGetCheckpoints.class, msg -> {
                    String checkPoints = ClientOperator.getInstance().getCheckpoints(msg.getEngineType(), msg.getPluginInfo(), msg.getJobIdentifier());
                    if (null == checkPoints) {
                        checkPoints = StringUtils.EMPTY;
                    }
                    sender().tell(checkPoints, getSelf());
                })
                .match(MessageContainerInfos.class, msg -> {
                    List<String> containerInfos = ClientOperator.getInstance().containerInfos(msg.getJobClient());
                    if (null == containerInfos) {
                        containerInfos = new ArrayList<>(0);
                    }
                    sender().tell(containerInfos, getSelf());
                })
                .match(MessageGetPluginDefaultConfig.class, msg -> {
                    List<ClientTemplate> defaultPluginConfig = ClientOperator.getInstance().getDefaultPluginConfig(msg.getEngineType(),msg.getConfigType());
                    if (null == defaultPluginConfig) {
                        defaultPluginConfig = new ArrayList<>(0);
                    }
                    sender().tell(defaultPluginConfig, getSelf());
                })
                .match(MessageTestConnectInfo.class,msg ->{
                    ComponentTestResult execute = ClientOperator.getInstance().testConnect(msg.getEngineType(), msg.getPluginInfo());
                    if(null == execute){
                        execute = new ComponentTestResult();
                    }
                    sender().tell(execute,getSelf());
                })
                .match(MessageExecuteQuery.class,msg ->{
                    List<List<Object>> execute = ClientOperator.getInstance().executeQuery(msg.getEngineType(), msg.getPluginInfo(),msg.getSql(),msg.getDatabase());
                    if(null == execute){
                        execute = new ArrayList<>();
                    }
                    sender().tell(execute,getSelf());
                })
                .match(MessageUploadInfo.class, msg -> {
                    String execute = ClientOperator.getInstance().uploadStringToHdfs(msg.getEngineType(), msg.getPluginInfo(), msg.getBytes(), msg.getHdfsPath());
                    if (null == execute) {
                        execute = StringUtils.EMPTY;
                    }
                    sender().tell(execute, getSelf());
                })
                .match(MessageResourceInfo.class, msg -> {
                    ClusterResource resource = ClientOperator.getInstance().getClusterResource(msg.getEngineType(), msg.getPluginInfo());
                    if (null == resource) {
                        resource = new ClusterResource();
                    }
                    sender().tell(resource, getSelf());
                })
                .match(MessageRollingLogBaseInfo.class, msg -> {
                    List<String> rollingLogBaseInfo = ClientOperator.getInstance().getRollingLogBaseInfo(msg.getEngineType(), msg.getPluginInfo(), msg.getJobIdentifier());
                    if (null == rollingLogBaseInfo && rollingLogBaseInfo.size() == 0) {
                        rollingLogBaseInfo = new ArrayList<>();
                    }
                    sender().tell(rollingLogBaseInfo, getSelf());
                })
                .build();
    }
}

