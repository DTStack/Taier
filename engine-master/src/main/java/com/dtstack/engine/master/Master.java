package com.dtstack.engine.master;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.dtstack.engine.common.WorkerInfo;

import java.util.LinkedList;
import java.util.List;

public class Master extends AbstractActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public List<WorkerInfo> workerInfos = new LinkedList<>();

    public Receive createReceive() {
        return receiveBuilder()
                .match(WorkerInfo.class, msg -> {workerInfos.add(msg);
                log.info(msg.getIp() + " is alive.");})
                .build();
    }


}
