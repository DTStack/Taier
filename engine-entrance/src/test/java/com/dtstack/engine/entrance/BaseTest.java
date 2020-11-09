package com.dtstack.engine.entrance;

import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.master.env.EnvironmentContext;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author yuebai
 * @date 2020-05-08
 */
@RunWith(DtCenterSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EnvironmentContext.class,  CacheConfig.class, MybatisConfig.class})
public class BaseTest {
}
