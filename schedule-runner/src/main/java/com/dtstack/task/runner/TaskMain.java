package com.dtstack.task.runner;


import com.dtstack.dtcenter.common.log.LogbackComponent;
import com.dtstack.dtcenter.common.util.SystemPropertyUtil;
import com.dtstack.task.common.Service;
import com.dtstack.task.common.env.EnvironmentContext;
import com.dtstack.task.server.zk.ZkConfig;
import com.dtstack.task.runner.config.*;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.Closeable;
import java.util.List;

/**
 * @author sishu.yss
 */
public class TaskMain {

    private static Logger logger = LoggerFactory.getLogger(TaskMain.class);

    private static List<String> classes = Lists.newArrayList("com.dtstack.task.web.WebService");

    private static List<Closeable> closeables = Lists.newArrayList();

    private static EnvironmentContext environmentContext;

    public static void main(String[] args) {
        try {
            logger.info("dt-center-task start begin...");
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();
            ApplicationContext context = new AnnotationConfigApplicationContext(
                    EnvironmentContext.class, CacheConfig.class, ThreadPoolConfig.class,
                    MybatisConfig.class, RdosBeanConfig.class, SdkConfig.class);
            environmentContext = (EnvironmentContext) context.getBean("environmentContext");
            setSystemProperty();
            initServices(context);
            new ShutDownHook(closeables).addShutDownHook();
            logger.info("dt-center-task start end...");
        } catch (Throwable t) {
            logger.error("start error:", t);
            System.exit(-1);
        }
    }

    private static void setSystemProperty() {
        SystemPropertyUtil.setHadoopUserName(environmentContext.getHadoopUserName());
    }

    private static void initServices(ApplicationContext context) throws Exception {
        for (String className : classes) {
            Class<? extends Service> c = Class.forName(className).asSubclass(Service.class);
            Service service = c.getConstructor(ApplicationContext.class).newInstance(context);
            service.initService();
            closeables.add(service);
        }
    }
}
