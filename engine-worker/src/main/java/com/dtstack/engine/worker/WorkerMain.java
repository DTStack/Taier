package com.dtstack.engine.worker;

import com.dtstack.dtcenter.common.util.SystemPropertyUtil;
import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.log.LogbackComponent;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.JobSubmitExecutor;
import com.dtstack.engine.service.task.HeartBeatListener;
import com.dtstack.engine.service.zookeeper.ZkDistributed;
import com.dtstack.engine.router.VertxHttpServer;
import com.dtstack.engine.worker.cache.LocalCacheSyncZkListener;
import com.dtstack.engine.worker.cache.ZkLocalCache;
import com.dtstack.engine.worker.cache.ZkSyncLocalCacheListener;
import com.dtstack.engine.worker.config.WorkConfig;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * Date: 2017年02月17日 下午8:57:21
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class WorkerMain {

    private static final Logger logger = LoggerFactory.getLogger(WorkerMain.class);

    private static VertxHttpServer vertxHttpServer;

    private static ZkDistributed zkDistributed;

    private static JobSubmitExecutor jobSubmitExecutor;

    public static void main(String[] args) throws Exception {
        try {
            SystemPropertyUtil.setSystemUserDir();
            LogbackComponent.setupLogger();
            // load config
            Map<String, Object> nodeConfig = new WorkConfig().loadConf();
            ConfigParse.setConfigs(nodeConfig);
            // init service
            initService(nodeConfig);
            // add hook
            ShutdownHookUtil.addShutdownHook(WorkerMain::shutdown, WorkerMain.class.getSimpleName(), logger);
        } catch (Throwable e) {
            logger.error("only engine-worker start error:{}", e);
            System.exit(-1);
        }
    }


    private static void initService(Map<String, Object> nodeConfig) throws Exception {
        jobSubmitExecutor = JobSubmitExecutor.getInstance();
        zkDistributed = ZkDistributed.createZkDistributed(nodeConfig).zkRegistration();
        vertxHttpServer = new VertxHttpServer(null,null);
        init(zkDistributed);

        logger.warn("start only engine-worker success...");
    }

    public static void init(ZkDistributed zkDistributed) {
        ZkLocalCache.getInstance().init(zkDistributed);
        WorkNode.getInstance().init();
		HeartBeatListener.init();
        LocalCacheSyncZkListener.init();
        ZkSyncLocalCacheListener.init();
    }

    private static void shutdown() {
        List<Closeable> closeables = Lists.newArrayList(vertxHttpServer, zkDistributed, jobSubmitExecutor);
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
