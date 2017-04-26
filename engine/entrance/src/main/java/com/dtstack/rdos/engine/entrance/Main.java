package com.dtstack.rdos.engine.entrance;

import com.dtstack.rdos.engine.entrance.command.CmdLineParams;
import com.dtstack.rdos.engine.entrance.command.OptionsProcessor;
import com.dtstack.rdos.engine.entrance.configs.YamlConfig;
import com.dtstack.rdos.engine.entrance.http.EHttpServer;
import com.dtstack.rdos.engine.entrance.log.LogComponent;
import com.dtstack.rdos.engine.entrance.log.LogbackComponent;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 
 * Reason: TODO ADD REASON(可选) Date: 2017年02月17日 下午8:57:21 Company:
 * www.dtstack.com
 * 
 * @author sishu.yss
 *
 */
public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	private static LogComponent logComponent = new LogbackComponent();

	private static EHttpServer eHttpServer;
	
	private static ZkDistributed zkDistributed;

	public static void main(String[] args) {
		try {
			CommandLine cmdLine = OptionsProcessor.parseArg(args);
			CmdLineParams.setLine(cmdLine);
			// set logger
			logComponent.setupLogger();
			// load config
			Map<String,Object> nodeConfig = loadConf();
			// init service
			initService(nodeConfig);
			// add hook
			addShutDownHook();
		} catch (Exception e) {
			logger.error("node start error:{}", e);
			System.exit(-1);
		}
	}

	
	@SuppressWarnings("unchecked")
	private static Map<String,Object> loadConf() throws Exception{
		Map<String,Object> nodeConfig = new YamlConfig().parse(CmdLineParams.getConfigFilePath(),Map.class);
		CheckEngineAgumentsNotNull.checkEngineAguments(nodeConfig);
		return nodeConfig;
	}
	
	
	private static void initService(Map<String,Object> nodeConfig) throws Exception{
		eHttpServer = new EHttpServer(nodeConfig);
		zkDistributed = ZkDistributed.createZkDistributed(nodeConfig).zkRegistration();
		JobSubmitExecutor.getInstance().init(nodeConfig);
	}
	
	private static void addShutDownHook(){
		new ShutDownHook(eHttpServer,zkDistributed,JobSubmitExecutor.getInstance()).addShutDownHook();
	}
}
