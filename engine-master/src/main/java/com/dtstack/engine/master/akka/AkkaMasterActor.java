package com.dtstack.engine.master.akka;

import akka.actor.AbstractActor;
import com.dtstack.engine.common.akka.message.WorkerInfo;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


public class AkkaMasterActor extends AbstractActor {

    private static final Logger logger = LoggerFactory.getLogger(AkkaMasterActor.class);

    public static final String GET_WORKER_INFOS = "getWorkerInfos";
    private final static String IP_PORT_TEMPLATE = "%s:%s";

    public HashMap<String, WorkerInfo> workerInfos = Maps.newHashMap();

    public Receive createReceive() {
        return receiveBuilder()
                .match(WorkerInfo.class, msg -> {
                    String ipAndPort = String.format(IP_PORT_TEMPLATE, msg.getIp(), msg.getPort());
                    workerInfos.put(ipAndPort, msg);
                    logger.info(ipAndPort + " is alive.");
                })
                .matchEquals(GET_WORKER_INFOS, msg -> {
                    sender().tell(workerInfos, getSelf());
                })
                .build();
    }


}

