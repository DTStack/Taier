package com.dtstack.engine.worker;

import com.dtstack.engine.common.log.LogbackComponent;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class WorkerMain {

    private static final Logger logger = LoggerFactory.getLogger(WorkerMain.class);


    public static void main(String[] args) throws Exception {
        try {
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();


            ShutdownHookUtil.addShutdownHook(WorkerMain::shutdown, WorkerMain.class.getSimpleName(), logger);
        } catch (Throwable e) {
            logger.error("only engine-worker start error:{}", e);
            System.exit(-1);
        }
    }


    private static void shutdown() {
        logger.info("Worker is shutdown...");
    }
}
