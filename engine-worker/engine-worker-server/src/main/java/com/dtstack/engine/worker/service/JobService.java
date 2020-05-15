package com.dtstack.engine.worker.service;

import akka.actor.AbstractActor;
import com.dtstack.engine.common.akka.message.*;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.ClientTemplate;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.client.ClientOperator;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JobService extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageJudgeSlots.class, msg -> {
                    boolean sufficient = ClientOperator.getInstance().judgeSlots(msg.getJobClient());
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
                    Object execute = ClientOperator.getInstance().testConnect(msg.getEngineType(), msg.getPluginInfo());
                    if(null == execute){
                        execute = new Object();
                    }
                    sender().tell(execute,getSelf());
                })
                .build();
    }
}

