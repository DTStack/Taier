package com.dtstack.engine.entrance;

import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.log.LogbackComponent;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.common.JobSubmitExecutor;
import com.dtstack.engine.entrance.config.EngineConfig;
import com.dtstack.engine.master.MasterMain;
import com.dtstack.engine.service.zookeeper.ZkDistributed;
import com.dtstack.engine.router.VertxHttpServer;
import com.dtstack.engine.worker.WorkerMain;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年02月17日 下午8:57:21
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class EngineMain {

	private static final Logger logger = LoggerFactory.getLogger(EngineMain.class);

	private static VertxHttpServer vertxHttpServer;

	private static ZkDistributed zkDistributed;

	private static JobSubmitExecutor jobSubmitExecutor;

	public static void main(String[] args) throws Exception {
		try {
			SystemPropertyUtil.setSystemUserDir();
			LogbackComponent.setupLogger();
			// load config
			Map<String,Object> nodeConfig = new EngineConfig().loadConf();
			ConfigParse.setConfigs(nodeConfig);
			// init service
			initService(nodeConfig);
			// add hook
			ShutdownHookUtil.addShutdownHook(EngineMain::shutdown, EngineMain.class.getSimpleName(), logger);
		} catch (Throwable e) {
			logger.error("Engine start error:{}", e);
			System.exit(-1);
		}
	}

	
	private static void initService(Map<String,Object> nodeConfig) throws Exception{
		jobSubmitExecutor = JobSubmitExecutor.getInstance();
		zkDistributed = ZkDistributed.createZkDistributed(nodeConfig).zkRegistration();
		vertxHttpServer = new VertxHttpServer(nodeConfig);
		WorkerMain.init(zkDistributed);
		MasterMain.init();

		logger.warn("start Engine success...");
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
