package com.dtstack.engine.worker;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.dtstack.engine.common.akka.Worker;
import com.dtstack.engine.common.log.LogbackComponent;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.worker.config.WorkerConfig;
import com.dtstack.engine.worker.listener.HeartBeatListener;
import com.dtstack.engine.worker.service.JobService;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.impl.ConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerMain implements Worker {

    private static final Logger logger = LoggerFactory.getLogger(WorkerMain.class);

    public static void main(String[] args) throws Exception {
        try {
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();

            WorkerConfig.loadConfig();

            String akkaRemoteWork = WorkerConfig.getWorkerSystemName();
            ActorSystem system = ActorSystem.create(akkaRemoteWork, ConfigFactory.load());

            // Create an actor
            system.actorOf(Props.create(JobService.class), akkaRemoteWork);

            String masterRemotePath = WorkerConfig.getMasterRemotePath();
            ActorSelection master = system.actorSelection(masterRemotePath);

            String workIp = WorkerConfig.getWorkerIp();
            int workerPort = Integer.parseInt(WorkerConfig.getWorkerPort());
            String workerRemotePath = WorkerConfig.getWorkerRemotePath();

            new HeartBeatListener(master, workIp, workerPort, workerRemotePath);

            ShutdownHookUtil.addShutdownHook(WorkerMain::shutdown, WorkerMain.class.getSimpleName(), logger);
        } catch (Throwable e) {
            logger.error("only engine-worker start error:{}", e);
            System.exit(-1);
        }
    }


    private static void shutdown() {
        logger.info("WorkerMain is shutdown...");
    }
}
