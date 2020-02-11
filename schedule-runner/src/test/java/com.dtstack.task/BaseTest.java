package com.dtstack.task;

import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.task.runner.config.CacheConfig;
import com.dtstack.task.runner.config.MybatisConfig;
import com.dtstack.task.runner.config.RdosBeanConfig;
import com.dtstack.task.runner.config.SdkConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

@RunWith(DtCenterSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EnvironmentContext.class, MybatisConfig.class, RdosBeanConfig.class, CacheConfig.class, SdkConfig.class})
public class BaseTest {

    protected final static Long ADMIN_USER_ID = 5L;
    protected final static Long DEFAULT_TENANT_ID = 3L;

    @Test
    public void demo() {

    }
}
