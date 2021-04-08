package com.dtstack.engine.master.akka;

import akka.actor.AbstractActor;
import com.dtstack.engine.common.akka.message.WorkerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;


public class AkkaMasterActor extends AbstractActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaMasterActor.class);

    public static final String GET_WORKER_INFOS = "getWorkerInfos";
    private final static String IP_PORT_TEMPLATE = "%s:%s";
    private final static String SUCCUSS_INFO = "Receive heartBeat success!";

    private Set<WorkerInfo> workerInfos = new HashSet<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(WorkerInfo.class, workerInfo -> {
                    String ipAndPort = String.format(IP_PORT_TEMPLATE, workerInfo.getIp(), workerInfo.getPort());
                    workerInfos.remove(workerInfo);
                    workerInfos.add(workerInfo);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(ipAndPort + " is alive.");
                    }
                    sender().tell(SUCCUSS_INFO, getSelf());
                })
                .matchEquals(GET_WORKER_INFOS, msg -> {
                    sender().tell(workerInfos, getSelf());
                })
                .build();
    }


}

