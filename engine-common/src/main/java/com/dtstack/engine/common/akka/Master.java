package com.dtstack.engine.common.akka;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.dtstack.engine.common.WorkerInfo;
import com.google.common.collect.Maps;

import java.util.HashMap;


public class Master extends AbstractActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public HashMap<String, WorkerInfo> workerInfos = Maps.newHashMap();

    public Receive createReceive() {
        return receiveBuilder()
                .match(WorkerInfo.class, msg -> {
                    workerInfos.put(msg.getIp()+ ":" + msg.getPort(), msg);
                    log.info(msg.getIp() + " is alive.");})
                .matchEquals("getWorkerInfos", msg -> {sender().tell(workerInfos, getSelf());})
                .build();
    }


}

