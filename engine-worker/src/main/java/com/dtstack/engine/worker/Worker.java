package com.dtstack.engine.worker;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.dtstack.engine.common.ClientOperator;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;

import java.util.List;

public class Worker extends AbstractActor{
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("judgeSlots", msg->{ log.info("judgeSlots");})
                .matchEquals("submitJob", msg->{ log.info("submitjob");})
                .matchEquals("getJobStatus", msg->{
                    RdosTaskStatus status = ClientOperator.getInstance().getJobStatus("engineType", "pluginInfo", JobIdentifier.createInstance("", "", ""));
                    sender().tell(status, getSelf());
                })
                .matchEquals("getEngineLog", msg->{
                    String engineLog = ClientOperator.getInstance().getEngineLog("engineType", "pluginInfo", JobIdentifier.createInstance("", "", ""));
                    sender().tell(engineLog, getSelf());
                })
                .matchEquals("getJobMaster", msg->{
                    String jobMaster = ClientOperator.getInstance().getJobMaster("engineType", "pluginInfo", JobIdentifier.createInstance("", "", ""));
                    sender().tell(jobMaster, getSelf());
                })
                .matchEquals("stopJob", msg->{
                    JobResult result = ClientOperator.getInstance().stopJob(new JobClient());
                    sender().tell(result, getSelf());

                })
                .matchEquals("getCheckpoints", msg->{
                    String checkPoints = ClientOperator.getInstance().getCheckpoints("engineType", "pluginInfo", JobIdentifier.createInstance("", "", ""));
                    sender().tell(checkPoints, getSelf());
                })
                .matchEquals("containerInfos", msg->{
                    List<String> containerInfos = ClientOperator.getInstance().containerInfos(new JobClient());
                    sender().tell(containerInfos, getSelf());
                })
                .build();
    }
}

