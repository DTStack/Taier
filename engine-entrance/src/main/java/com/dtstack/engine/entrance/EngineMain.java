package com.dtstack.engine.entrance;

import com.dtstack.engine.common.security.NoExitSecurityManager;
import com.dtstack.engine.common.util.JavaPolicyUtils;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.master.EngineApplication;
import com.dtstack.engine.worker.WorkerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/29
 */
public class EngineMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineMain.class);

    public static void main(String[] args){
        try {
            EngineApplication.main(args);
            // add hook
            ShutdownHookUtil.addShutdownHook(EngineMain::shutdown, EngineMain.class.getSimpleName(), LOGGER);
            System.setSecurityManager(new NoExitSecurityManager());
            JavaPolicyUtils.checkJavaPolicy();
        } catch (Throwable e) {
            LOGGER.error("EngineMain start error:", e);
            System.exit(-1);
        }
    }

    private static void shutdown() {
        LOGGER.info("EngineMain is shutdown...");
    }
}
