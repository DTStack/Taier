package com.dtstack.engine.worker;

import com.dtstack.engine.common.security.NoExitSecurityManager;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.worker.jobdealer.TaskLogStoreDealer;
import com.dtstack.engine.remote.annotation.EnableRemoteClient;
import com.dtstack.engine.worker.log.LogbackComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRemoteClient(basePackage = "com.dtstack.engine.common.api")
public class WorkerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerMain.class);

    public static void main(String[] args){
        try {
            LOGGER.info("engine-worker start begin...");
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();
            new SpringApplication(WorkerMain.class).run(args);
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
