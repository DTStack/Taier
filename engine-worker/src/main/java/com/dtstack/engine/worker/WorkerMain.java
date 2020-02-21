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

import java.io.*;
import java.util.Properties;
import java.util.concurrent.*;


public class WorkerMain {

    private static final Logger logger = LoggerFactory.getLogger(WorkerMain.class);


    public static void main(String[] args) throws Exception {
        try {
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();


            ShutdownHookUtil.addShutdownHook(WorkerMain::shutdown, WorkerMain.class.getSimpleName(), logger);

            Properties properties = loadConfig();
            String name = properties.getProperty("AkkaRemoteWork", "akkaRemoteWork");

            ActorSystem system = ActorSystem.create(name, ConfigFactory.load());
            // Create an actor

            String path = properties.getProperty("masterRemotePath");
            //"akka.tcp://AkkaRemoteMaster@127.0.0.1:2552/user/Master"

            ActorSelection toMaster = system.actorSelection(path);
            String ip = properties.getProperty("workIp");
            int port = Integer.parseInt(properties.getProperty("workPort"));
            String workRemotePath = properties.getProperty("workRemotePath");
            WorkerInfo workInfo = new WorkerInfo(ip, port, workRemotePath, System.currentTimeMillis());

            Runnable runnable = new WorkerListener(toMaster, workInfo);
            ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
            singleThreadExecutor.execute(runnable);
        } catch (Throwable e) {
            logger.error("only engine-worker start error:{}", e);
            System.exit(-1);
        }
    }


    private static void shutdown() {
        logger.info("Worker is shutdown...");
    }

    private static Properties loadConfig() throws IOException {
        Properties properties = new Properties();
        String file = System.getProperty("user.dir") + "/conf/worker.properties";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        properties.load(bufferedReader);
        return properties;
    }
}
