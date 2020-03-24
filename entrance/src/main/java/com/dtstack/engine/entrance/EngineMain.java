package com.dtstack.engine.entrance;

import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.security.NoExitSecurityManager;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.entrance.configs.YamlConfig;
import com.dtstack.engine.entrance.log.LogbackComponent;
import com.dtstack.engine.service.zk.ZkDistributed;
import com.dtstack.engine.web.VertxHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	public static void main(String[] args) throws Exception {
		try {
			SystemPropertyUtil.setSystemUserDir();
			LogbackComponent.setupLogger();
			// load config
			Map<String,Object> nodeConfig = new YamlConfig().loadConf();
			ConfigParse.setConfigs(nodeConfig);
			// init service
			initService(nodeConfig);
			// add hook
			addShutDownHook();
			System.setSecurityManager(new NoExitSecurityManager());
		} catch (Throwable e) {
			logger.error("node start error:{}", e);
			System.exit(-1);
		}
	}

	
	private static void initService(Map<String,Object> nodeConfig) throws Exception{

		zkDistributed = ZkDistributed.createZkDistributed(nodeConfig).zkRegistration();

		vertxHttpServer = new VertxHttpServer(nodeConfig);

		logger.warn("start engine success...");

	}
	
	private static void addShutDownHook(){
		new ShutDownHook(vertxHttpServer,zkDistributed).addShutDownHook();
	}
}
