package com.dtstack.engine.worker;

import akka.actor.ActorRef;
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
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerMain implements Worker {

    private static final Logger logger = LoggerFactory.getLogger(WorkerMain.class);

    public static void main(String[] args) throws Exception {
        try {
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();

            String workerSystemName = WorkerConfig.getWorkerSystemName();
            ActorSystem system = ActorSystem.create(workerSystemName, ConfigFactory.load());
            Config config = system.settings().config();
            WorkerConfig.loadConfig(config);

            // Create an actor
            String workerName = WorkerConfig.getWorkerName();
            ActorRef actorRef = system.actorOf(Props.create(JobService.class), workerName);

//            String masterRemotePath = WorkerConfig.getMasterRemotePath();
            String masterRemotePath = null;//todo
            ActorSelection master = system.actorSelection(masterRemotePath);

            String workIp = WorkerConfig.getWorkerIp();
            int workerPort = WorkerConfig.getWorkerPort();
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
