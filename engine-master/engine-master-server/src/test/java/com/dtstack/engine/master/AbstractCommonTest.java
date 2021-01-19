package com.dtstack.engine.master;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MasterServerBeanConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@RunWith(DtCommonSpringRunner.class)
@ContextConfiguration(classes = {EnvironmentContext.class, MasterServerBeanConfig.class, CacheConfig.class, MybatisConfig.class})
@SpringBootTest
public abstract class AbstractCommonTest {
}
