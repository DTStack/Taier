package com.dtstack.engine.master;

import com.dtstack.dtcenter.common.util.SystemPropertyUtil;
import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.log.LogbackComponent;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MasterConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.master.config.RdosBeanConfig;
import com.dtstack.engine.master.config.SdkConfig;
import com.dtstack.engine.master.config.ThreadPoolConfig;
import com.dtstack.engine.master.task.HeartBeatCheckListener;
import com.dtstack.engine.master.task.LogStoreListener;
import com.dtstack.engine.master.task.MasterListener;
import com.dtstack.engine.service.task.HeartBeatListener;
import com.dtstack.engine.service.zookeeper.ZkDistributed;
import com.dtstack.engine.router.VertxHttpServer;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * Date: 2017年02月17日 下午8:57:21
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class MasterMain {

    private static final Logger logger = LoggerFactory.getLogger(MasterMain.class);

    private static VertxHttpServer vertxHttpServer;

    private static ZkDistributed zkDistributed;

    private static EnvironmentContext environmentContext;

    public static void main(String[] args) throws Exception {
        try {
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();

            ApplicationContext context = new AnnotationConfigApplicationContext(
                    EnvironmentContext.class, CacheConfig.class, ThreadPoolConfig.class,
                    MybatisConfig.class, RdosBeanConfig.class, SdkConfig.class);
            environmentContext = (EnvironmentContext) context.getBean("environmentContext");
            setSystemProperty();

            // load config
            Map<String, Object> nodeConfig = new MasterConfig().loadConf();
            ConfigParse.setConfigs(nodeConfig);
            // init service
            initService(context);
            // add hook
            ShutdownHookUtil.addShutdownHook(MasterMain::shutdown, MasterMain.class.getSimpleName(), logger);
        } catch (Throwable e) {
            logger.error("only engine-master start error:{}", e);
            System.exit(-1);
        }
    }

    private static void setSystemProperty() {
        SystemPropertyUtil.setHadoopUserName(environmentContext.getHadoopUserName());
    }

    private static void initService(ApplicationContext context) throws Exception {
        zkDistributed = ZkDistributed.createZkDistributed(null).zkRegistration();
        vertxHttpServer = new VertxHttpServer(context, environmentContext);
        init();

        logger.warn("start only engine-master success...");
    }

    public static void init() {
        MasterListener masterListener = new MasterListener();
        HeartBeatCheckListener.init(masterListener);
        LogStoreListener.init(masterListener);
        HeartBeatListener.init();
    }

    private static void shutdown() {
        List<Closeable> closeables = Lists.newArrayList(vertxHttpServer, zkDistributed);
        for (Closeable closeable : closeables) {
            if (closeables != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }
}
