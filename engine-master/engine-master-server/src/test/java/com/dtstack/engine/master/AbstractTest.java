package com.dtstack.engine.master;

import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.listener.RunnerListener;
import com.dtstack.engine.master.utils.ValueUtils;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;


/**
 * 继承此类写单元测试会调用测试库，使用之前会删除测试库的数据，并自动拉取DataCollection中的数据写入数据库。
 * 如果你想要编写评估覆盖率的单测用例，请使用该父类，并将所需支持的数据写在DataCollection中。
 */
@Component
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(DtCenterSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EnvironmentContext.class, CacheConfig.class, MybatisConfig.class})
@PowerMockIgnore({"javax.management.*", "javax.security.*", "javax.net.ssl.*", "javax.crypto.*"})
@SpringBootTest
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
