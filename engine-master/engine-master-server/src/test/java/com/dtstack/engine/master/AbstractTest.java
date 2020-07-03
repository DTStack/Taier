package com.dtstack.engine.master;

import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MasterServerBeanConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.master.config.ThreadPoolConfig;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.listener.RunnerListener;
import com.dtstack.engine.master.utils.ValueUtils;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@Component
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(DtCenterSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EnvironmentContext.class, MasterServerBeanConfig.class, CacheConfig.class, ThreadPoolConfig.class,
        MybatisConfig.class})
@PowerMockIgnore({"javax.management.*", "javax.security.*", "javax.net.ssl.*", "javax.crypto.*"})
public abstract class AbstractTest implements RunnerListener {

    @Autowired
    public ApplicationContext context;

    @Override
    public void runsBeforeClass() {
        try {
            ValueUtils.initData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void runsAfterClass() {
    }
}
