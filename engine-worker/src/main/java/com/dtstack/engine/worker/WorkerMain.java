package com.dtstack.engine.worker;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.dtstack.engine.common.WorkerInfo;
import com.dtstack.engine.common.log.LogbackComponent;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class WorkerMain {

    private static final Logger logger = LoggerFactory.getLogger(WorkerMain.class);


    public static void main(String[] args) throws Exception {
        try {
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();


            ShutdownHookUtil.addShutdownHook(WorkerMain::shutdown, WorkerMain.class.getSimpleName(), logger);

            // TODO: 2020/2/14 创建远程actor发送心跳信息
            ActorSystem system = ActorSystem.create("AkkaRemoteWork", ConfigFactory.load("worker.conf"));
            // Create an actor
            ActorSelection toMaster = system.actorSelection("akka.tcp://AkkaRemoteMaster@127.0.0.1:2552/user/Master");
            WorkerInfo workInfo = new WorkerInfo("127.0.0.1", 123);
            Runnable runnable = new WorkerListener(toMaster, workInfo);
            // 线程池优化
            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
            executor.submit(runnable);
        } catch (Throwable e) {
            logger.error("only engine-worker start error:{}", e);
            System.exit(-1);
        }
    }


    private static void shutdown() {
        logger.info("Worker is shutdown...");
    }
}
