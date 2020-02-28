package com.dtstack.engine.worker.listener;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.akka.message.WorkerInfo;
import com.dtstack.engine.common.util.LogCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HeartBeatListener implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatListener.class);

    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 2000;

    private ActorSelection actorSelection;
    private String ip;
    private int port;
    private String path;


    public HeartBeatListener(ActorSelection actorSelection, String ip, int port, String path) {
        this.actorSelection = actorSelection;
        this.ip = ip;
        this.port = port;
        this.path = path;

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("HeartBeatListener"));
        scheduledService.scheduleWithFixedDelay(
                this,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        WorkerInfo workerInfo = new WorkerInfo(ip, port, path, System.currentTimeMillis());
        actorSelection.tell(workerInfo, ActorRef.noSender());
        if (LogCountUtil.count(logOutput, MULTIPLES)) {
            logger.info("HeartBeatListener Running...");
        }
    }
}

