package com.dtstack.engine.master;

import com.dtstack.engine.common.Service;
import com.dtstack.engine.common.log.LogbackComponent;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.master.config.MasterServerBeanConfig;
import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.master.config.RdosBeanConfig;
import com.dtstack.engine.master.config.ThreadPoolConfig;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.router.RouterService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/29
 */
public class MasterMain {

    private static final Logger logger = LoggerFactory.getLogger(MasterMain.class);

    private static final List<Class<? extends Service>> SERVICES = new ArrayList<Class<? extends Service>>();

    static {
        SERVICES.add(RouterService.class);
    }

    private static final List<Closeable> CLOSEABLES = Lists.newArrayList();


    private static EnvironmentContext environmentContext;

    public static void main(String[] args) throws Exception {
        try {
            setSystemProperty();
            LogbackComponent.setupLogger();

            ApplicationContext context = new AnnotationConfigApplicationContext(
                    EnvironmentContext.class, MasterServerBeanConfig.class, CacheConfig.class, ThreadPoolConfig.class,
                    MybatisConfig.class, RdosBeanConfig.class);
            environmentContext = (EnvironmentContext) context.getBean(EnvironmentContext.class);

            setHadoopUserName();
            // init service
            initServices(context);

            // add hook
            ShutdownHookUtil.addShutdownHook(MasterMain::shutdown, MasterMain.class.getSimpleName(), logger);
        } catch (Throwable e) {
            logger.error("engine-master start error:", e);
            System.exit(-1);
        }
    }

    private static void setSystemProperty() {
        SystemPropertyUtil.setSystemUserDir();
    }

    private static void setHadoopUserName() {
        SystemPropertyUtil.setHadoopUserName(environmentContext.getHadoopUserName());
    }

    private static void initServices(ApplicationContext context) throws Exception {
        for (Class<? extends Service> serviceClass : SERVICES) {
            Class<? extends Service> c = serviceClass.asSubclass(Service.class);
            Service service = c.getConstructor(ApplicationContext.class).newInstance(context);
            service.initService();
            CLOSEABLES.add(service);
        }
    }

    private static void shutdown() {
        for (Closeable closeable : CLOSEABLES) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }

        logger.info("MasterMain is shutdown...");
    }
}
