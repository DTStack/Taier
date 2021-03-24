package com.dtstack.engine.worker;

import com.dtstack.engine.common.security.NoExitSecurityManager;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.jobdealer.TaskLogStoreDealer;
import com.dtstack.engine.worker.log.LogbackComponent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerMain.class);

    public static void main(String[] args){
        try {
            LOGGER.info("engine-worker start begin...");
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();
            Config workerConfig = AkkaConfig.init(ConfigFactory.load());
            TaskLogStoreDealer.getInstance();

            if (!AkkaConfig.isLocalMode()) {
                AkkaWorkerServerImpl.getAkkaWorkerServer().start(workerConfig);
            }
            ShutdownHookUtil.addShutdownHook(WorkerMain::shutdown, WorkerMain.class.getSimpleName(), LOGGER);
            System.setSecurityManager(new NoExitSecurityManager());
            LOGGER.info("engine-worker start end...");
        } catch (Throwable e) {
            LOGGER.error("engine-worker start error:", e);
            System.exit(-1);
        }
    }


    private static void shutdown() {
        LOGGER.info("WorkerMain is shutdown...");
    }
}
