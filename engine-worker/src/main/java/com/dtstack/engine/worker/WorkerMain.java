package com.dtstack.engine.worker;

import com.dtstack.engine.common.log.LogbackComponent;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerMain {

    private static final Logger logger = LoggerFactory.getLogger(WorkerMain.class);

    public static void main(String[] args) throws Exception {
        try {
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();
            Config workerConfig = AkkaConfig.checkIpAndPort(ConfigFactory.load());
            AkkaWorkerServerImpl.getAkkaWorkerServer().start(workerConfig);

            ShutdownHookUtil.addShutdownHook(WorkerMain::shutdown, WorkerMain.class.getSimpleName(), logger);
        } catch (Throwable e) {
            logger.error("engine-worker start error:{}", e);
            System.exit(-1);
        }
    }


    private static void shutdown() {
        logger.info("WorkerMain is shutdown...");
    }
}
