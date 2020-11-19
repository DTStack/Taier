package com.dtstack.engine;

import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.DtCenterSpringJUnit4ClassRunner;
import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MasterServerBeanConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.master.env.EnvironmentContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 2:39 下午 2020/10/15
 */
@Component
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(DtCenterSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EnvironmentContext.class, MasterServerBeanConfig.class, CacheConfig.class, MybatisConfig.class})
@PowerMockIgnore({"javax.management.*", "javax.security.*", "javax.net.ssl.*", "javax.crypto.*"})
@SpringBootTest
public class TestParamsToConf {


    @Test
    public void testGetParamConf(){

        String params = "## Driver程序使用的CPU核数,默认为1\n" +
                "# driver.cores=1\n" +
                "\n" +
                "## Driver程序使用内存大小,默认512m\n" +
                "# driver.memory=512m\n" +
                "\n" +
                "## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\n" +
                "## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\n" +
                "# driver.maxResultSize=1g\n" +
                "\n" +
                "## SparkContext 启动时是否记录有效 SparkConf信息,默认false\n" +
                "# logConf=false\n" +
                "\n" +
                "## 启动的executor的数量，默认为1\n" +
                "executor.instances=1\n" +
                "\n" +
                "## 每个executor使用的CPU核数，默认为1\n" +
                "executor.cores=1\n" +
                "\n" +
                "## 每个executor内存大小,默认512m\n" +
                "# executor.memory=512m\n" +
                "\n" +
                "## 任务优先级, 值越小，优先级越高，范围:1-1000\n" +
                "job.priority=10\n" +
                "\n" +
                "## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\n" +
                "# logLevel = INFO\n" +
                "\n" +
                "## spark中所有网络交互的最大超时时间\n" +
                "# spark.network.timeout=120s\n" +
                "\n" +
                "## executor的OffHeap内存，和spark.executor.memory配置使用\n" +
                "# spark.yarn.executor.memoryOverhead";
        try {
            Properties properties = PublicUtil.stringToProperties(params);
            System.out.println(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
