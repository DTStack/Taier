package com.dtstack.engine.worker;

import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.akka.config.AkkaLoad;
import com.dtstack.engine.common.security.NoExitSecurityManager;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.worker.log.LogbackComponent;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerMain {

    private static final Logger logger = LoggerFactory.getLogger(WorkerMain.class);

    public static void main(String[] args){
        try {
            logger.info("engine-worker start begin...");
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();
            String property = System.getProperty("user.dir");
            Config workerConfig = AkkaConfig.init(AkkaLoad.load(property+"/conf/"));

            AkkaWorkerServerImpl.getAkkaWorkerServer().start(workerConfig);
            ShutdownHookUtil.addShutdownHook(WorkerMain::shutdown, WorkerMain.class.getSimpleName(), logger);
            System.setSecurityManager(new NoExitSecurityManager());
            logger.info("engine-worker start end...");
        } catch (Throwable e) {
            logger.error("engine-worker start error:", e);
            System.exit(-1);
        }
    }


    private static void shutdown() {
        logger.info("WorkerMain is shutdown...");
    }
}
