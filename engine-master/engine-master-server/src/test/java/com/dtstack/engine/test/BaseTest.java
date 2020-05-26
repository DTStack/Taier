package com.dtstack.engine.test;

import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MasterServerBeanConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.master.config.ThreadPoolConfig;
import com.dtstack.engine.master.env.EnvironmentContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class BaseTest {
    private final static String DICTIONARY_NAME = "DAGScheduleX";
    public static ApplicationContext context = null;

    @BeforeClass
    public static void beforeOperation() {
        //获得项目文件的根目录
        String s_pre = System.getProperty("user.dir");
        int index = s_pre.indexOf(DICTIONARY_NAME);
        System.setProperty("user.dir", s_pre.substring(0, index + DICTIONARY_NAME.length()));
        //获取上下文环境变量，用于Spring解析
        context = new AnnotationConfigApplicationContext(
                EnvironmentContext.class, MasterServerBeanConfig.class, CacheConfig.class, ThreadPoolConfig.class,
                MybatisConfig.class);
    }

    @Test
    public abstract void testOperation();
}
