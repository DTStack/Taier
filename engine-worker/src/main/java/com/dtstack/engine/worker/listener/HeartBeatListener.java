package com.dtstack.engine.worker.listener;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.WorkerInfo;
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
    private final ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("HeartBeatListener"));

    private ActorSelection actorSelection;
    private WorkerInfo workerInfo;


    public HeartBeatListener(ActorSelection actorSelection, WorkerInfo workerInfo) {
        this.actorSelection = actorSelection;
        this.workerInfo = workerInfo;

        scheduledService.scheduleWithFixedDelay(
                this,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        actorSelection.tell(workerInfo, ActorRef.noSender());
        if (LogCountUtil.count(logOutput, MULTIPLES)) {
            logger.info("HeartBeatListener Running...");
        }
    }
}

