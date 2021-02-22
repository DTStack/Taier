package com.dtstack.engine.worker.worker;

import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.worker.AkkaWorkerServerImpl;
import com.dtstack.engine.worker.CommonUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/11/26
 */
public class TestWorker {

    @Test
    public void testWorkerInit() {
        CommonUtils.setUserDirToTest();
        Config workerConfig = AkkaConfig.init(ConfigFactory.load());
        AkkaWorkerServerImpl.getAkkaWorkerServer().start(workerConfig);
    }
}
