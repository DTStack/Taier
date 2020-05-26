package com.dtstack.engine.test;

import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MasterServerBeanConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.master.config.ThreadPoolConfig;
import com.dtstack.engine.master.env.EnvironmentContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;


@RunWith(DtCenterSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EnvironmentContext.class, MasterServerBeanConfig.class, CacheConfig.class, ThreadPoolConfig.class,
        MybatisConfig.class})
public abstract class BaseTest {

    @Autowired
    public  ApplicationContext context = null;

    @Test
    public abstract void testOperation();
}
